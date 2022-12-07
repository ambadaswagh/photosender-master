package com.cwidanage.photoftp.services;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.adobe.xmp.XMPConst;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.adobe.xmp.XMPException;
import com.cwidanage.photoftp.models.EventDetail;
import com.cwidanage.photoftp.models.Photo;
import com.cwidanage.photoftp.models.Queue;
import com.cwidanage.photoftp.photocorrupt.util.DetectPhotoCorrupt;
import com.cwidanage.photoftp.photocorrupt.util.ImageFormat;
import com.cwidanage.photoftp.photocorrupt.util.ImageScanner;
import com.cwidanage.photoftp.repository.EventDetailRepository;
import com.cwidanage.photoftp.repository.PhotoRepository;
import com.cwidanage.photoftp.repository.QueueRepository;
import com.cwidanage.photoftp.util.EmailTemplate;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.xmp.XmpDirectory;
import java.util.Date;

/**
 * @author Chathura Widanage
 */
@Service
@EnableScheduling
@EnableAsync
@ConfigurationProperties(prefix = "photoftp.receiver")
public class FileWalker {

    private final static Logger logger = LogManager.getLogger(FileWalker.class);
    private Semaphore semaphore = new Semaphore(1);
    private String source;
    private String processed;
    private String backup;
    private File sourceFolder;
    private File processedFolder;

    private long delay;

    private HashMap<String, FileSceneLog> fileMap = new HashMap<>();

    @Autowired
    private PhotoRepository photoRepository;
    @Autowired
    private EmailTemplate emailTemplate;

    @Autowired
    private EventDetailRepository eventRepository;

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private EmailService emailService;
    
	@Autowired
    private EventService eventService;

    class FileSceneLog {

        long firstScene;
        long lastScene;
        long lastLength;

        public FileSceneLog(File file) {
            this.updateLogAndCheck(file);
        }

        boolean updateLogAndCheck(File file) {
            if (this.firstScene == 0) {
                this.firstScene = System.currentTimeMillis();
            }
            this.lastScene = System.currentTimeMillis();
            long newLength = file.length();
            if (newLength != this.lastLength) {//file is still writing
                this.firstScene = System.currentTimeMillis();
                this.lastLength = newLength;
                return false;
            }
            this.lastLength = newLength;
            return this.lastScene - this.firstScene > delay;
        }
    }

    @PostConstruct
    public void init() throws IOException {
        this.sourceFolder = new File(this.source);
        this.processedFolder = new File(this.processed);
        logger.debug("Source is: " + source);
        logger.debug("Processed is: " + processed);
        FileUtils.forceMkdir(this.sourceFolder);
        FileUtils.forceMkdir(this.processedFolder);
    }

    @Async
    @Scheduled(fixedDelay = 10000)
    public void walk() {
        boolean acquired = semaphore.tryAcquire();
        if (!acquired) {
            logger.debug("Failed to acquire lock.");
            return;
        }
        logger.debug("Walking on files");
        try {
            List<String> filesList = Arrays.asList(this.sourceFolder.list());
            for (String fileName : filesList) {
                File sourcePhoto = new File(this.source, fileName);
                if (this.isValidPhoto(sourcePhoto)) {
                    if (this.isCompletelyWritten(sourcePhoto)) {
                        if (!this.fileMap.containsKey(sourcePhoto.getName())) {//adding file to checker
                            this.fileMap.put(sourcePhoto.getName(), new FileSceneLog(sourcePhoto));
                        } else if (this.fileMap.get(sourcePhoto.getName()).updateLogAndCheck(sourcePhoto)) {
                            Photo photo = new Photo();
                            photo.setFileName(sourcePhoto.getName());
                            String nextFileName ="";
                            if(this.processPhoto(photo, sourcePhoto)) {
                                nextFileName = getFileNameFromEvent(photo);
                                nextFileName = (nextFileName != null) ? nextFileName : fileName;
                                File destinationPhoto = new File(this.processedFolder, nextFileName);
                                photo.setFileName(nextFileName);
                                if (!destinationPhoto.exists()) {
                                    logger.debug("Copying source photo to processed folder {}", sourcePhoto);
                                    FileUtils.copyFile(sourcePhoto, destinationPhoto);
                                    logger.debug("Successfully moved source photo to processed folder {}", sourcePhoto);
                                } else {
                                    logger.debug("Already present a copy of file {}", sourcePhoto.getName());
                                }
                            }
                            photo.setOriginalFileName(fileName);
                            photoRepository.save(photo);
                            eventService.updateProcessedPhotoCount(photo.getTransmissionData());
                            logger.debug("Deleting source photo {}", sourcePhoto.getName());
                            
                            //copy file before deleting
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = new Date();
                            File newDir=new File(this.backup+"/"+dateFormat.format(date));
                            FileUtils.forceMkdir(newDir);
                            File destFile = new File(this.backup+"/"+dateFormat.format(date)+"/", fileName);            
                            FileUtils.copyFile(sourcePhoto, destFile);
                            
                            FileUtils.forceDelete(sourcePhoto);
                            logger.debug("Successfully deleted source photo {}", sourcePhoto.getName());
                            this.fileMap.remove(sourcePhoto.getName());//clear cache
                        }
                    }
                } else {
                    logger.debug("Invalid photo detected : {}", sourcePhoto.getName());
                }
            }
        } catch (IOException e) {
            logger.error("File operation failed.", e);
        } finally {
            semaphore.release();
        }
    }

    private String getFileNameFromEvent(Photo photo) {
        EventDetail event = eventRepository.findDistinctByEventCodeEquals(photo.getTransmissionData());
        String fileName = null;
        if (event != null) {
        	int photosCount = photoRepository.countByTransmissionDataAndErrorFalse(event.getEventCode());
        	String nextCount = String.format("%03d", photosCount + 1);
            fileName = event.getfilename();
            fileName = fileName.replaceAll(" ", "_") + "_" + nextCount + "." + FilenameUtils.getExtension(photo.getFileName());
        }
        return fileName;
    }

    private boolean isValidPhoto(File sourcePhoto) {
        String extension = FilenameUtils.getExtension(sourcePhoto.getName());
        return !sourcePhoto.isDirectory()
                && (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg"));
    }

    private boolean processPhoto(final Photo photo, final File photoFile) {
        final AtomicBoolean isProcessed = new AtomicBoolean(true);
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(photoFile);
            Collection<XmpDirectory> xmpDirectories = metadata.getDirectoriesOfType(XmpDirectory.class);
            if (xmpDirectories.isEmpty()) {
                photo.setError(true);
                photo.setLog("No XMP data present");
                logger.error("No XMP data present in the image {}", photoFile);
                isProcessed.set(false);
            } else {
                xmpDirectories.forEach(xmpDirectory -> {
                    try {
                        String transmissionData = xmpDirectory.getXMPMeta().getPropertyString("http://ns.adobe.com/photoshop/1.0/",
                                "TransmissionReference");
                        logger.info("Transmission data {}", transmissionData);
                        photo.setTransmissionData(transmissionData);
                        if (transmissionData==null) {
                            logger.warn("No Transmission Data Found");
                            photo.setTransmissionData("Empty TransmissionData");
                            isProcessed.set(false);
                        }  else if (transmissionData.length() > 8) {
                            photo.setTransmissionData(transmissionData.substring(0, 8));
                        }

                        {
                        String description = xmpDirectory.getXMPMeta().getPropertyString("http://ns.adobe.com/photoshop/1.0/",
                                    "Caption");
                            logger.info("Description {}", description);
                           photo.setDescription(description);
                        }
                        {
                            String headline = xmpDirectory.getXMPMeta().getPropertyString("http://ns.adobe.com/photoshop/1.0/",
                                    "Headline");
                            logger.info("Headline {}", headline);
                            photo.setHeadline(headline);
                        }
                        {
                            String credit = xmpDirectory.getXMPMeta().getPropertyString("http://ns.adobe.com/photoshop/1.0/",
                                    "Credit");
                            logger.info("Credit {}", credit);
                            photo.setCredit(credit);
                        }
                        {
                            String creator = xmpDirectory.getXMPMeta().getArrayItem(XMPConst.NS_DC, "creator",1).getValue();
                            logger.info("Creator {}", creator);
                            photo.setCreator(creator);
                        }



                        //OK state
                        logger.debug("Transmission data for {} : {}", photoFile.getName(), photo.getTransmissionData());
                        EventDetail event = eventRepository.findDistinctByEventCodeEquals(photo.getTransmissionData());
                        //logger.debug("Event found is: " + event.toString());

                        //DetectPhotoCorrupt detectPhotoCorrupt;
                       

                        //detectPhotoCorrupt = new DetectPhotoCorrupt(photoFile, true);
                    
                        ImageScanner.Result result = ImageScanner.scan(photoFile, Objects.requireNonNull(ImageFormat.fromFileName(photoFile.getName())));
                        logger.debug("detect photo corrupt => "+result.isCorrupt());
                       // logger.debug("is file complete=>"+detectPhotoCorrupt.isFileComplete());
                        if (result.isCorrupt()) {
                            logger.debug("Corrupt photo detected : {}", photoFile.getName());
                            logger.warn("no image sent as corrupted image : {} for {}", photo.getTransmissionData(), photo.getFileName());
                            setPhotoError(photo, "no image sent as corrupted image : " + photo.getFileName());
                            isProcessed.set(false);
                            if (event == null) {
                                String eventTemplate = emailTemplate.FTP_NO_EVENT_FILE_EMAIL_TEMPLATE;
                                logger.debug("Sending email  xmp@sportphoto24.com");
                                emailService.sendEmailWithAttachment(photo.getFileName(),"The picture has wrong Transmission Reference", (new Date()).toString(),eventTemplate, "xmp@sportphoto24.com,info@sportphoto24.com", photoFile.getAbsolutePath());
                                logger.debug("Email Sent to  xmp@sportphoto24.com");
                            }else
                            {
                                String eventTemplate = emailTemplate.FTP_CORRUPT_FILE_EMAIL_TEMPLATE;
                                eventTemplate = eventTemplate.replace("{{eventName}}", event.getName());
                                eventTemplate = eventTemplate.replace("{{eventCode}}", event.getEventCode());
                                eventTemplate = eventTemplate.replace("{{source}}", event.getSource());
                                eventTemplate = eventTemplate.replace("{{CiUrlWork}}", event.getCiUrlWork());
                                eventTemplate = eventTemplate.replace("{{CiEmailWork}}", event.getCiEmailWork());
                                eventTemplate = eventTemplate.replace("{{CiTelWork}}", event.getCiTelWork());
                                eventTemplate = eventTemplate.replace("{{Creator}}", event.getCreator());
                                eventTemplate = eventTemplate.replace("<<photographername>>",event.getCreator());
                                logger.debug("Email Sending to photographer");
                                emailService.sendEmailWithAttachment(photo.getFileName(),"File Upload Error the file arrived corrupted", (new Date()).toString(),eventTemplate, event.getPemail()+",xmp@sportphoto24.com,info@sportphoto24.com", photoFile.getAbsolutePath());
                                logger.debug("Email Sent to Photographer named: "+event.getCreator());
                            }
                        } else if (event == null) {
                            logger.warn("No event matching the transmission id : {} for {}", photo.getTransmissionData(), photo.getFileName());
                            setPhotoError(photo, "No event matching the transmission id : " + photo.getTransmissionData());
                            String eventTemplate = emailTemplate.FTP_NO_EVENT_FILE_EMAIL_TEMPLATE;
                            emailService.sendEmailWithAttachment(photo.getFileName(), "The picture has wrong Transmission Reference",(new Date()).toString(),eventTemplate, "xmp@sportphoto24.com,info@sportphoto24.com", photoFile.getAbsolutePath());
                            isProcessed.set(false);
                        } else if (event.getDestination() == null) {
                            logger.warn("No valid ftp group defined for this event : {}[{}] of {}",
                                    event.getName(), event.getId(), photo.getFileName());
                            setPhotoError(photo, String.format("No valid ftp groups defined for this event : %s[%d]",
                                    event.getName(), event.getId()));
                            isProcessed.set(false);
                        } else if (event.getDestination().getFtpAccounts() == null
                                || event.getDestination().getFtpAccounts().isEmpty()) {
                            logger.warn("Empty of invalid set of ftp destination for this event : {}[{}] of {}",
                                    event.getName(), event.getId(), photo.getFileName());
                            setPhotoError(photo, String.format("Empty of invalid set of ftp destination for this event : %s[%d]",
                                    event.getName(), event.getId()));
                            isProcessed.set(false);
                        } else {
                            List<Queue> queueList = event.getDestination().getFtpAccounts().stream().map(
                                    ftp -> {
                                        Queue queue = new Queue();
                                        queue.setPhoto(photo);
                                        queue.setFtpAccount(ftp);
                                        return queue;
                                    }
                            ).collect(Collectors.toList());
                            photo.setQueue(queueList);
                            logger.info("{} queues created for {}", queueList.size(), photo.getFileName());
                        }
                    } catch (XMPException e) {
                        isProcessed.set(false);
                        setPhotoError(photo, String.format("Error reading xmp data : %s", e.getMessage()));
                        logger.error("Error reading xmp data", e);
                    } catch (IOException e) {
                        isProcessed.set(false);
                        setPhotoError(photo, String.format("Error reading corrupt photo data : %s", e.getMessage()));
                        logger.error("Error reading corrupt photo data", e);
                        //java.util.logging.Logger.getLogger(FileWalker.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }
        } catch (IOException e) {
            isProcessed.set(false);
            logger.error("Error in reading XMP data from image", e);
            setPhotoError(photo, "Error in reading XMP data from image : " + e.getMessage());
        } catch (ImageProcessingException e) {
            isProcessed.set(false);
            logger.error("Error in reading XMP data", e);
            setPhotoError(photo, "Error in reading XMP data : " + e.getMessage());
        }
        return isProcessed.get();
    }

    public static boolean isCompletelyWritten(File file) {
        RandomAccessFile stream = null;
        try {
            stream = new RandomAccessFile(file, "rw");
            return true;
        } catch (Exception e) {
            logger.info("Skipping file " + file.getName() + " for this iteration due it's not completely written");
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    logger.error("Exception during closing file " + file.getName());
                }
            }
        }
        return false;
    }

    private void setPhotoError(Photo photo, String log) {
        photo.setError(true);
        photo.setLog(log);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getProcessed() {
        return processed;
    }

    public void setProcessed(String processed) {
        this.processed = processed;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

	public String getBackup() {
		return backup;
	}

	public void setBackup(String backup) {
		this.backup = backup;
	}
    
}
