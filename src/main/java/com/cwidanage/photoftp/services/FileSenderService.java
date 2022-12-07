package com.cwidanage.photoftp.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

import com.cwidanage.photoftp.repository.PhotoRepository;
import com.jcraft.jsch.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.ftp.session.FtpSession;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cwidanage.photoftp.models.EventDetail;
import com.cwidanage.photoftp.models.FTPAccount;
import com.cwidanage.photoftp.models.Photo;
import com.cwidanage.photoftp.models.Queue;
import com.cwidanage.photoftp.repository.EventDetailRepository;
import com.cwidanage.photoftp.repository.QueueRepository;
import com.cwidanage.photoftp.util.AES;
import com.cwidanage.photoftp.util.EmailTemplate;
import java.io.IOException;

/**
 * @author Chathura Widanage
 */
@Service
@EnableScheduling
@EnableAsync
@ConfigurationProperties(prefix = "photoftp.sender")
public class FileSenderService {

    private static final Logger logger = LogManager.getLogger(FileSenderService.class);

    @Autowired
    private PhotoRepository photoRepository;
    
    @Autowired
    private Environment environment;

    private String source;

    JSch jsch = new JSch();

    private Semaphore semaphore = new Semaphore(1);

    private HashMap<String, DefaultFtpSessionFactory> ftpSessionFactories = new HashMap<>();

    private HashMap<String, Session> sftpSessionFactories = new HashMap<>();

    private int threads = 5;

    private ExecutorService executorService = Executors.newFixedThreadPool(threads);
    private CompletionService<String> completionService = new ExecutorCompletionService<String>(executorService);

    private Map<Integer, Queue> currentQueue = new ConcurrentHashMap<>();
    private BlockingQueue<Integer> blockingQueue = new LinkedBlockingDeque<>();
    private List<String> inProcess = Collections.synchronizedList(new ArrayList<>());

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private EventDetailRepository eventDetailRepository;

    @Autowired
    private EmailService emailService;
    @Autowired
    private EmailTemplate emailTemplate;
    
	@Autowired
    private EventService eventService;

    private List<String> erroredFileList = new ArrayList<String>();

    //@PostConstruct
    public void initThreads() {
        for (int i = 0; i < threads; i++) {
            executorService.submit(() -> {
                while (!currentQueue.isEmpty()) {
                    Integer queueId = null;
                    try {
                        queueId = blockingQueue.poll(1, TimeUnit.DAYS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        continue;
                    }
                    String fileDateAdded = "";
                    if (queueId != null) {
                        logger.debug("Processing queue {}", queueId);
                        Queue queue = currentQueue.get(queueId);
                        fileDateAdded = queue.getDateAdded().toString();
                        if (queue == null) {
                            continue;
                        }
                        File sourceFile = new File(this.source, queue.getPhoto().getFileName());
                        try (FileInputStream fileInputStream = new FileInputStream(sourceFile)) {
                            FTPAccount ftpAccount = queue.getFtpAccount();
                            logger.debug("FTP ACCOUNT =>"+ftpAccount.getId());

                            if (ftpAccount!=null &&(ftpAccount.getEmail() != null && ftpAccount.getEmail()!="")) {
                                try {
                                    String eventCode = queue.getPhoto().getTransmissionData();
                                    String pEmail = ftpAccount.getEmail();
                                    logger.debug("P-EMAIL VALUE "+pEmail);
                                    String description=queue.getPhoto().getDescription();
                                    String eventTemplate = emailTemplate.FTP_FILE_UPLOAD_EMAIL_TEMPLATE;
                                    logger.debug("2nd log pEmail value => "+pEmail);
                                    if (pEmail == null || "".equals(pEmail)) {
                                        logger.error("Error: No Email Id Found, pEmail => "+pEmail);
                                    } else {
                                        try {
                                            EventDetail findDistinctByEventCodeEquals = eventDetailRepository.findDistinctByEventCodeEquals(eventCode);
                                            String headline = findDistinctByEventCodeEquals.getHeadline();
                                            eventTemplate = eventTemplate.replace("<<headline>>", headline);
                                            eventTemplate = eventTemplate.replace("{{fileName}}", sourceFile.getName());
                                            eventTemplate = eventTemplate.replace("{{eventName}}", findDistinctByEventCodeEquals.getName());
                                            eventTemplate = eventTemplate.replace("{{eventCode}}", findDistinctByEventCodeEquals.getEventCode());
                                            eventTemplate = eventTemplate.replace("{{source}}", findDistinctByEventCodeEquals.getSource());
                                            eventTemplate = eventTemplate.replace("{{CiUrlWork}}", findDistinctByEventCodeEquals.getCiUrlWork());
                                            eventTemplate = eventTemplate.replace("{{CiEmailWork}}", findDistinctByEventCodeEquals.getCiEmailWork());
                                            eventTemplate = eventTemplate.replace("{{CiTelWork}}", findDistinctByEventCodeEquals.getCiTelWork());
                                            eventTemplate = eventTemplate.replace("{{Creator}}", findDistinctByEventCodeEquals.getCreator());
                                            if(description == null || description.isEmpty())
                                                description =" ";
                                            eventTemplate = eventTemplate.replace("<<description>>", description);
                                            logger.debug("Email Sending to FTP Email address");
                                            emailService.sendEmailWithAttachment(sourceFile.getName(),"Pictrures from " +headline, (new Date()).toString(), eventTemplate, pEmail, sourceFile.getAbsolutePath());
                                            logger.debug("Email Sent to FTP Email address");
                                            queue.setProcessed(true);
                                        } catch (Exception e) {
                                            logger.error("Error: No Email Id Found");
                                            queue.setError(true);
                                            queue.setLog("Photo Email sending failed : " + e.getMessage());
                                            queue.setProcessed(true);
                                        }
                                    }
                                } catch (Exception ex) {
                                    logger.error("Error in sending {} to {}", queue.getPhoto().getFileName(),
                                            queue.getFtpAccount().getHost(), ex);
                                    queue.setError(true);
                                    queue.setLog("Photo Email sending failed : " + ex.getMessage());
                                    queue.setProcessed(true);
                                }
                            } else {
                                String subFolder = null;

                                //handle directory
                                if (ftpAccount.getDirectory() != null && !ftpAccount.getDirectory().trim().isEmpty()) {
                                    subFolder = ftpAccount.getDirectory();
                                    if (subFolder.charAt(0) == '/') {
                                        subFolder = subFolder.substring(1);
                                    }

                                    if (subFolder.charAt(subFolder.length() - 1) == '/') {
                                        subFolder = subFolder.substring(0, subFolder.length() - 1);
                                    }
                                }

                                //handle create sub folder by event name
                                if (ftpAccount.getCreateFolder() != null && ftpAccount.getCreateFolder()) {
                                    String eventCode = queue.getPhoto().getTransmissionData();

                                    if (subFolder == null) {
                                        subFolder = "";
                                    } else {
                                        subFolder += "/";
                                    }
                                    EventDetail eventDetail = eventDetailRepository.findDistinctByEventCodeEquals(eventCode);
                                    Date eventDate = new Date(eventDetail.getEventDate());
                                    //subFolder += (eventDetail.getName().replaceAll(" ", "_").concat("_sportphoto24_"+ DateTimeFormatter.ofPattern("dd-MM-yyyy").format(eventDate.toInstant().atZone(ZoneId.systemDefault()))));
                                    subFolder += DateTimeFormatter.ofPattern("yyyy-MM-dd").format(eventDate.toInstant().atZone(ZoneId.systemDefault())) + "_" + (eventDetail.getName().replaceAll(" ", "_").concat("_sportphoto24"));
                                    logger.debug("Sub folder structure to create {}", subFolder);
                                }
                                Session sftpSession;
                                FtpSession session = null;
                                ChannelSftp channel =null;
                                if(ftpAccount.getSftpEnabled()){
                                    sftpSession = getDefaultSftpSessionFactory(ftpAccount);
                                    channel = (ChannelSftp) sftpSession.openChannel("sftp");
                                    channel.connect();
                                    SftpATTRS attrs=null;
                                    try {
                                        attrs = channel.stat(channel.getHome()+"/"+ (subFolder != null ? (subFolder + "/") : ""));
                                    } catch (Exception e) {
                                        logger.debug("trying to get the folder error: "+e.getMessage());
                                    }
                                    if(attrs == null){
                                        channel.mkdir(channel.getHome()+"/"+ (subFolder != null ? (subFolder + "/") : ""));
                                        logger.info("Directory not found");
                                    }else {
                                        logger.info("Directory already created !");
                                    }
                                }else {
                                    session = getDefaultFtpSessionFactory(ftpAccount).getSession();
                                    if (!session.exists(session.getClientInstance().printWorkingDirectory() + (subFolder != null ? (subFolder + "/") : ""))) {
                                        String folders[] = subFolder.split("/");
                                        String folderToCreate = ((FtpSession)session).getClientInstance().printWorkingDirectory();
                                        for (String fldr : folders) {
                                            folderToCreate += ("/" + fldr);
                                            if (!session.exists(folderToCreate)) {
                                                session.mkdir(folderToCreate);
                                            }
                                        }
                                        //session.mkdir(session.getClientInstance().printWorkingDirectory() + (subFolder != null ? (subFolder + "/") : ""));
                                    }
                                }
                                Boolean check = true;
                                try {
                                    logger.debug("Before writing the file to the dest folder.");
                                    if(channel!=null && channel.isConnected()) {
                                            logger.info("Writing file using the SFTP session.");
                                            channel.put(fileInputStream,
                                                    (channel.getHome() + (subFolder != null ? (subFolder + "/") : "") + sourceFile.getName()), ChannelSftp.RESUME);
                                    }else{
                                        session.write(fileInputStream,
                                                ((FtpSession)session).getClientInstance().printWorkingDirectory() + (subFolder != null ? (subFolder + "/") : "") + sourceFile.getName()
                                        );
                                    }

                                } catch (IOException ex) {
                                    check = false;
                                    ex.getMessage();
                                    logger.error("Error in sending {} to {}", queue.getPhoto().getFileName(),
                                            queue.getFtpAccount().getHost(), ex);
                                    queue.setError(true);
                                    queue.setProcessed(true);
                                    String errorLog = "Photo sending failed : " + ex.getMessage();
                                    errorLog = errorLog.replace("write to '/", "write to '");
                                    queue.setLog(errorLog);
                                    erroredFileList.add(sourceFile.getName());
                                }
                                if(session!=null){
                                    session.close();
                                    logger.info("FTP Connection closed !");
                                }
                                if(channel!=null){
                                    channel.disconnect();
                                    logger.info("SFTP Channel disconnected !!");
                                }
                                queue.setProcessed(true);
                                if (check) {
                                    queue.setLog("");//clear the log
                                }
                            }
                        } catch (Exception e) {
                            logger.error("Error in sending {} to {}", queue.getPhoto().getFileName(),
                                    queue.getFtpAccount().getHost(), e);
                            queue.setError(true);
                            queue.setLog("Photo sending failed : " + e.getCause().getMessage());                            
                        } finally {
                            currentQueue.remove(queue.getId());
                            if (queue.isError() && queue.getProcessedTime() != null && queue.getProcessedTime().getTime() - queue.getDateAdded().getTime() > 3600000) {
                                logger.debug("Stop trying {} due to two days failure", queue.getId());
                                queue.setProcessed(true);
                                queue.setLog("Stopped retrying after trying for two days. "
                                        + "Couldn't send due to following error : " + queue.getLog());
                            }
                            queue.setProcessedTime(new Date());
                            queueRepository.save(queue);
                        }

                        String errorFileListString = "";
                        for (int errorFile = 0; errorFile < erroredFileList.size(); errorFile++) {
                            errorFileListString = errorFileListString + erroredFileList.get(errorFile) + "\n";
                        }
                        if (erroredFileList.size() > 0) {
                            logger.debug("Sending Email Alert Notification");
                            emailService.sendEmailAlertWithAttachment(errorFileListString, fileDateAdded.split(" ")[0], queue.getFtpAccount());
                            logger.debug("Email Alert Sent.....");
                            errorFileListString = "";
                        }
                        erroredFileList = new ArrayList<String>();
                        logger.info("END OF photo processing !!!");
                    }
                }
            });
        }
    }

    @Scheduled(fixedDelay = 10000)
    public void sendFiles() {
        logger.info("START SCHEDULE");
        /*try {
            finished = executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            logger.error("threads termination error"+e.getMessage());
        }
        try {
            while (!executorService.isTerminated()) {

                final Future<String> future = completionService.take();
                logger.info(future.get());
            }
        } catch (ExecutionException | InterruptedException ex) {
            logger.debug(ex.getMessage());
        }*/
        if (!semaphore.tryAcquire()) {
            logger.info("Current queue size ="+currentQueue.size());
            return;
        }
        logger.trace("Acquired semaphore");
        try {
            List<Queue> toProcess = queueRepository.findByProcessedFalseOrderByDateAddedDesc();
            logger.debug("{} files in queue", toProcess.size());
            toProcess.stream().forEach(queue -> {
                File sourceFile = new File(this.source, queue.getPhoto().getFileName());
                if (!inProcess.contains(sourceFile.getName()) && FileWalker.isCompletelyWritten(sourceFile)) { //SP24_FR_SERIE_A_PAR-V-INT_230321_054.JPG
                    //QUEUE ID=> 1179, Time => Fri Mar 26 22:37:27 CET 2021
                    logger.debug("==========================");
                    logger.debug("File Name: " + queue.getPhoto().getFileName());
                    logger.debug("File Path: " + sourceFile.getAbsolutePath());
                    logger.debug("==========================");
                    if (sourceFile != null) {
                        Photo photo = queue.getPhoto();
                        logger.debug("Photo Data: " + photo.getTransmissionData());
                        logger.debug("==========================");
                        EventDetail detail = eventDetailRepository.findDistinctByEventCodeEquals(photo.getTransmissionData());
                        String output = "";
                        try {
                            logger.info("QUEUE ID=> "+queue.getId() +", Time => "+new Date());
                            output = writeCustomData(sourceFile, detail);
                            photo.setJobReference(getJobReference(detail, sourceFile));
                            photoRepository.save(photo);
                            eventService.updateProcessedPhotoCount(photo.getTransmissionData());
                        } catch (Exception ex) {
                            logger.error("Exception occured :" + ex);
                        }
                        logger.debug("==========================");
                        logger.debug("Out put is: " + output);
                        logger.debug("It's Done:");
                        logger.debug("==========================");
                    }
                }
                synchronized (currentQueue) {
                    if (!currentQueue.containsKey(queue.getId())) {
                        logger.debug("Adding {} to current queue", queue.getId());
                        currentQueue.put(queue.getId(), queue);
                        try {
                            blockingQueue.put(queue.getId());
                        } catch (InterruptedException e) {
                            logger.error("NOT ABLE TO ADD TO BLOCKING QUEUE=>" + queue.getId());
                            currentQueue.remove(queue.getId());
                        }
                    } else {
                        logger.debug("{} is already in current queue", queue.getId());
                    }
                }
            });
            logger.debug("Clearing in Process List");
            inProcess.clear();
        } catch (Exception e) {
            logger.error("Error occurred when executing scheduled file sending service", e);
        } finally {
            semaphore.release();
            logger.info("END SCHEDULE");
        }
        initThreads();
        //executorService.shutdown();
        while(true) {
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public DefaultFtpSessionFactory getDefaultFtpSessionFactory(FTPAccount ftpAccount) {
        String key = ftpAccount.getHost() + ftpAccount.getName() + ftpAccount.getUsername();
        if (!this.ftpSessionFactories.containsKey(key)) {
        	 
                DefaultFtpSessionFactory defaultFtpSessionFactory = new DefaultFtpSessionFactory();
                defaultFtpSessionFactory.setHost(ftpAccount.getHost());
                defaultFtpSessionFactory.setUsername(ftpAccount.getUsername());
                defaultFtpSessionFactory.setPassword(AES.decrypt(ftpAccount.getPassword(), environment.getProperty("secretKey")));
                defaultFtpSessionFactory.setClientMode(2);
                this.ftpSessionFactories.put(key, defaultFtpSessionFactory);

                return this.ftpSessionFactories.get(key);
            }
            return this.ftpSessionFactories.get(key);
    }

    public Session getDefaultSftpSessionFactory(FTPAccount ftpAccount) {
        String key = ftpAccount.getHost() + ftpAccount.getName() + ftpAccount.getUsername();
        if(ftpAccount.getSftpEnabled()){
            if (!this.sftpSessionFactories.containsKey(key)) {
                    Session channelSftp = setupJsch(ftpAccount);
                    this.sftpSessionFactories.put(key, channelSftp);
                    if(channelSftp == null)
                        logger.error("SFTP Session factory is NULL. Please check!");

                return this.sftpSessionFactories.get(key);
            }
            logger.info("SFTP Session already exists for this key : "+key);
            return this.sftpSessionFactories.get(key);
        }
        logger.info("No SFTP Account. Please try with FTP Config");
        return null;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    private HashMap<String, String> createMapOfData(EventDetail detail) {
        HashMap<String, String> data = new HashMap<String, String>();
        //data.put("id", String.valueOf(detail.getId()));
        //data.put("Code", detail.getCode());

        //data.put("Name", detail.getName());
        data.put("Event", detail.getName());
        return data;
    }

    private HashMap<String, String> createMapOfXmpRights(EventDetail detail) {
        HashMap<String, String> data = new HashMap<String, String>();

        //xmp xmp-xmprights
        data.put("UsageTerms", detail.getUsageTerms());
        data.put("WebStatement", detail.getWebStatement());

        return data;
    }

    private HashMap<String, String> createMapOfXmpDcTags(EventDetail detail) {
        HashMap<String, String> data = new HashMap<String, String>();

        //xmp xmp-dc
        data.put("Creator", detail.getCreator());
        data.put("Title", detail.getTitle());
        data.put("Rights", detail.getRights());
        //data.put("Subject", detail.getSubject());

        return data;
    }

    private HashMap<String, String> createMapOfXmpPhotoshopTags(EventDetail detail, File sourceFile) {
        HashMap<String, String> data = new HashMap<String, String>();

        //xmp -xmp-photoshop
        data.put("AuthorsPosition", detail.getAuthorsPosition());
        data.put("CaptionWriter", detail.getCaptionWriter());
        data.put("Category", detail.getCategory());
        data.put("City", detail.getCity());
        data.put("Country", detail.getCountry());
        data.put("Credit", detail.getCredit());
        data.put("HeadLine", detail.getHeadline());
        data.put("Source", detail.getSource());
        data.put("State", detail.getState());
        data.put("Instructions", detail.getInstructions());
        data.put("TransmissionReference", getJobReference(detail, sourceFile));
        //data.put("SupplementalCategories", detail.getSubcat() + ", " + detail.getSubcat1() + ", " + detail.getSubcat2());
        return data;
    }

    private HashMap<String, String> createMapOfXmpIptcExtTags(EventDetail detail) {
        HashMap<String, String> data = new HashMap<String, String>();

        //xmpcoretags -xmp-iptcext
        data.put("CreatorName", detail.getCreator());
        data.put("Event", detail.getName());
        data.put("OrganisationInImageName", "");
        data.put("OrganisationInImageCode", "");



        return data;
    }

    private HashMap<String, String> createMapOfXmpIptcCoreTags(EventDetail detail) {
        HashMap<String, String> data = new HashMap<String, String>();

        //xmpcoretags -xmp-iptccore
        data.put("CountryCode", detail.getCountryCode());
        data.put("CreatorAddress", detail.getCiAdrExtadr());
        data.put("CreatorCity", detail.getCiAdrCity());
        data.put("CreatorRegion", detail.getCiAdrRegion());
        data.put("CreatorPostalCode", detail.getCiAdrPcode());
        data.put("CreatorCountry", detail.getCiAdrCtry());
        data.put("CreatorWorkTelephone", detail.getCiTelWork());
        data.put("CreatorWorkEmail", detail.getCiEmailWork());
        data.put("CreatorWorkUrl", detail.getCiUrlWork());
        data.put("Location", detail.getLocation());
        data.put("SubjectCode", detail.getSubjectCode());
        data.put("Scene", "");

        return data;
    }

    private HashMap<String, String> createMapOfIPTC(EventDetail detail, File sourceFile) {
        HashMap<String, String> data = new HashMap<String, String>();

        //xmpcoretags -xmp-IPTC
        data.put("Headline", detail.getHeadline());
        //data.put("Keywords", detail.getSubject());
        data.put("SpecialInstructions", detail.getInstructions());
        data.put("By-line", detail.getCreator());
        data.put("Credit", detail.getCredit());
        data.put("Source", detail.getSource());
        data.put("CopyrightNotice", detail.getRights());
        data.put("Province-State", detail.getState());
        data.put("Sub-location", detail.getLocation());
        data.put("City", detail.getCity());
        data.put("Country-PrimaryLocationName", detail.getCountry());
        data.put("Country-PrimaryLocationCode", detail.getCountryCode());
        data.put("Writer-Editor", detail.getCaptionWriter());
        data.put("OriginalTransmissionReference",getJobReference(detail, sourceFile));
        //data.put("OriginalTransmissionReference", detail.getfilename());
        //data.put("OriginalTransmissionReference",sourceFile.getName().substring(5));
        data.put("Category", detail.getCategory());
        data.put("ObjectName", detail.getTitle());
        //data.put("SupplementalCategories", detail.getSubcat() + ", " + detail.getSubcat1() + ", " + detail.getSubcat2()+ ", ");

        return data;
    }

    private HashMap<String, String> createMapOfIFD0(EventDetail detail) {
        HashMap<String, String> data = new HashMap<String, String>();

        //xmpcoretags -xmp-IFD0
        data.put("Software", "FTPSender");
        data.put("Copyright", detail.getRights());
        data.put("Artist", detail.getCreator());

        return data;
    }

    private HashMap<String, String> createMapOfPhotoshop(EventDetail detail) {
        HashMap<String, String> data = new HashMap<String, String>();

        //xmpcoretags -Photoshop
        data.put("URL", detail.getCiUrlWork());

        return data;
    }

    private HashMap<String, String> createMapOfXmpPhotoshopTagss(EventDetail detail) {
        HashMap<String, String> data = new HashMap<String, String>();

        //xmp -xmp-photoshop
        data.put("SupplementalCategories", detail.getSubcat() + "," + detail.getSubcat1() + "," + detail.getSubcat2() + ",");
        return data;
    }

    private HashMap<String, String> createMapOfIPTCKey(EventDetail detail) {
        HashMap<String, String> data = new HashMap<String, String>();

        //xmpcoretags -xmp-IPTCkey
        data.put("Keywords", detail.getSubject());
        return data;
    }

    private HashMap<String, String> createMapOfXmpDcKeywords(EventDetail detail) {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("Subject", detail.getSubject());

        return data;
    }

    private HashMap<String, String> createMapOfXmpiptcExt(EventDetail detail) {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("LocationCreatedCity", detail.getCity());
        data.put("LocationCreatedProvinceState", detail.getCity());
        data.put("LocationCreatedCountryName", detail.getCountry());
        data.put("LocationCreatedCountryCode", detail.getCountryCode());
        return data;
    }
    
    private HashMap<String, String> createMapOfXMPplus(EventDetail detail) {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("LicensorID", "");
        data.put("LicensorName", detail.getSource());
        data.put("LicensorStreetAddress", detail.getCiAdrExtadr());
        data.put("LicensorCity", detail.getCiAdrCity());
        data.put("LicensorRegion", detail.getCiAdrRegion());
        data.put("LicensorCountry", detail.getCiAdrCtry());
        data.put("LicensorPostalCode", detail.getCiAdrPcode());
        data.put("LicensorURL", detail.getCiUrlWork());
        data.put("LicensorEmail", detail.getCiEmailWork());
        data.put("LicensorTelephone1", detail.getCiTelWork());
        data.put("CopyrightOwnerName", detail.getSource());
        data.put("CopyrightOwnerID", detail.getSource());
        data.put("ImageSupplierName", detail.getSource());
        data.put("ImageSupplierID", detail.getSource());
        data.put("ImageCreatorID", detail.getSource());
        data.put("ImageCreatorName", detail.getSource());



        return data;
    }

    private String writeCustomData(File sourceFile, EventDetail detail) throws Exception {
        logger.debug("Entered in method writeCustomData");
        inProcess.add(sourceFile.getName());
        StringBuffer output = new StringBuffer();
        HashMap<String, String> data1 = createMapOfData(detail);
        HashMap<String, String> data2 = createMapOfXmpRights(detail);
        HashMap<String, String> data3 = createMapOfXmpDcTags(detail);
        HashMap<String, String> data4 = createMapOfXmpPhotoshopTags(detail, sourceFile);
        HashMap<String, String> data5 = createMapOfXmpIptcExtTags(detail);
        HashMap<String, String> data6 = createMapOfXmpIptcCoreTags(detail);
        HashMap<String, String> data7 = createMapOfIPTC(detail,sourceFile);
        HashMap<String, String> data8 = createMapOfIFD0(detail);
        HashMap<String, String> data9 = createMapOfPhotoshop(detail);
        HashMap<String, String> data10 = createMapOfXmpPhotoshopTagss(detail);
        HashMap<String, String> data11 = createMapOfIPTCKey(detail);
        HashMap<String, String> data12 = createMapOfXmpDcKeywords(detail);
        HashMap<String, String> data13 = createMapOfXmpiptcExt(detail);
        HashMap<String, String> data0 = createMapOfXmpDcKeywords(detail);
        HashMap<String, String> data14 = createMapOfXmpDcKeywords(detail);
        HashMap<String, String> data15 = createMapOfXMPplus(detail);
        ArrayList<String> commands = new ArrayList<>();

        commands.add("exiftool");

        commands.add("-overwrite_original");


        for (Map.Entry<String, String> entry : data14.entrySet()) {
            if (entry.getValue() != null) {
                String[] tagsArray = entry.getValue().split(",");
                for (String tag : tagsArray) {
                    commands.add("-XMP-dc:Subject" + "<" + "XMP-iptcExt:" + "PersonInImage");
                }
            }
        }

        for (Map.Entry<String, String> entry : data14.entrySet()) {
            if (entry.getValue() != null) {
                String[] tagsArray = entry.getValue().split(",");
                for (String tag : tagsArray) {
                    commands.add("-IPTC:Keywords" + "<" + "xmp-iptcExt:"+ "PersonInImage");
                }
            }
        }

        for (Map.Entry<String, String> entry : data1.entrySet()) {
            if (entry.getValue() != null) {
                //command.append("-" + entry.getKey() + "=\"" + entry.getValue().replaceAll("\n", "").replaceAll("\r", "") + "\" ");
                commands.add("-" + entry.getKey() + "=" + entry.getValue().replaceAll("\n", "").replaceAll("\r", ""));
            }
        }
        for (Map.Entry<String, String> entry : data2.entrySet()) {
            if (entry.getValue() != null) {
                commands.add("-xmp-xmprights:" + entry.getKey() + "=" + entry.getValue().replaceAll("\n", "").replaceAll("\r", ""));
            }
        }

        for (Map.Entry<String, String> entry : data3.entrySet()) {
            if (entry.getValue() != null) {
                commands.add("-xmp-dc:" + entry.getKey() + "=" + entry.getValue().replaceAll("\n", "").replaceAll("\r", ""));
            }
        }

        for (Map.Entry<String, String> entry : data4.entrySet()) {
            if (entry.getValue() != null) {
                commands.add("-xmp-photoshop:" + entry.getKey() + "=" + entry.getValue().replaceAll("\n", "").replaceAll("\r", ""));
            }
        }

        for (Map.Entry<String, String> entry : data5.entrySet()) {
            if (entry.getValue() != null) {
                commands.add("-xmp-iptcExt:" + entry.getKey() + "=" + entry.getValue().replaceAll("\n", "").replaceAll("\r", ""));
            }
        }

        for (Map.Entry<String, String> entry : data6.entrySet()) {
            if (entry.getValue() != null) {
                commands.add("-xmp-iptcCore:" + entry.getKey() + "=" + entry.getValue().replaceAll("\n", "").replaceAll("\r", ""));
            }
        }

        for (Map.Entry<String, String> entry : data7.entrySet()) {
            if (entry.getValue() != null) {
                commands.add("-IPTC:" + entry.getKey() + "=" + entry.getValue().replaceAll("\n", "").replaceAll("\r", ""));
            }
        }

        for (Map.Entry<String, String> entry : data8.entrySet()) {
            if (entry.getValue() != null) {
                commands.add("-IFD0:" + entry.getKey() + "=" + entry.getValue().replaceAll("\n", "").replaceAll("\r", ""));
            }
        }

        for (Map.Entry<String, String> entry : data9.entrySet()) {
            if (entry.getValue() != null) {
                commands.add("-Photoshop:" + entry.getKey() + "=" + entry.getValue().replaceAll("\n", "").replaceAll("\r", ""));
            }
        }

        for (Map.Entry<String, String> entry : data10.entrySet()) {
            if (entry.getValue() != null) {
                String[] tagsArray = entry.getValue().split(",");
                for (String tag : tagsArray) {
                    commands.add("-xmp-photoshop:" + entry.getKey() + "=" + tag);
                }
            }
        }

        for (Map.Entry<String, String> entry : data11.entrySet()) {
            if (entry.getValue() != null) {
                String[] tagsArray = entry.getValue().split(";");
                for (String tag : tagsArray) {
                    commands.add("-IPTC:" + entry.getKey() + "=" + tag);
                }
            }
        }

        for (Map.Entry<String, String> entry : data12.entrySet()) {
            if (entry.getValue() != null) {
                String[] tagsArray = entry.getValue().split(";");
                for (String tag : tagsArray) {
                    commands.add("-xmp-dc:" + entry.getKey() + "=" + tag);
                }
            }
        }

        for (Map.Entry<String, String> entry : data13.entrySet()) {
            if (entry.getValue() != null) {
                commands.add("-xmp-iptcExt:" + entry.getKey() + "=" + entry.getValue().replaceAll("\n", "").replaceAll("\r", ""));
            }
        }

        
         for (Map.Entry<String, String> entry : data15.entrySet()) {
            if (entry.getValue() != null) {
                commands.add("-XMP-plus:" + entry.getKey() + "=" + entry.getValue().replaceAll("\n", "").replaceAll("\r", ""));
            }
        }

        
        commands.add(sourceFile.getAbsolutePath());
        //commands.add("\"" + sourceFile.getAbsolutePath() + "\"");
        String[] cmd = new String[commands.size()];
        for (int i = 0; i < cmd.length; i++) {
            cmd[i] = commands.get(i);
        }
        System.out.println("Size is: " + cmd.length);
        ProcessBuilder pb = new ProcessBuilder(cmd);

        try {
            pb.redirectErrorStream(true);
            Process p = pb.start();
            logger.debug("== Program Executed == ");
            p.waitFor();
            logger.debug("Wait For Response");
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            logger.debug("Obtained Response and Reader is: " + reader.toString());
            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
            reader.close();
            p.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("Reached to return: ");
        return output.toString();
    }

    private String getJobReference(EventDetail eventDetail, File sourceFile){
        String[] split = sourceFile.getName().split("_");
        String sequence = split[split.length-1].split("\\.")[0];
        return "SP24-" + eventDetail.getId()+"-"+sequence;
    }
    private Session setupJsch(FTPAccount ftpAccount)  {
        try {
            Session jschSession = jsch.getSession(ftpAccount.getUsername(), ftpAccount.getHost());
            jschSession.setPassword(ftpAccount.getPassword());
            jschSession.setPort(22);
            jschSession.setConfig("StrictHostKeyChecking", "no");
            jschSession.connect();
            return jschSession;
        }catch (JSchException e){
            logger.error(e.getMessage());
            return null;
        }
    }

}
