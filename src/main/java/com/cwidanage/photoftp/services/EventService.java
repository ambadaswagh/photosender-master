package com.cwidanage.photoftp.services;

import com.cwidanage.photoftp.exceptions.ValidationException;
import com.cwidanage.photoftp.models.EventDetail;
import com.cwidanage.photoftp.models.Photo;
import com.cwidanage.photoftp.repository.EventDetailRepository;
import com.cwidanage.photoftp.repository.PhotoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import javax.annotation.PostConstruct;

/**
 * @author Chathura Widanage
 */
@Service
public class EventService extends AbstractService {

    @Autowired
    private EventDetailRepository eventDetailRepository;
    
    @Autowired
    private PhotoRepository photoRepository;
    
    @PostConstruct
	public void init() throws IOException {
    	this.setProcessedFilesCount();
    }

    public Page<EventDetail> list(int pageNumber, int pageSize) {
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
//        return eventDetailRepository.findAll(pageRequest);
            return eventDetailRepository.findAllByOrderByIdDesc(pageRequest);
    }
    
	public void updateProcessedPhotoCount(String code) {
		EventDetail event = eventDetailRepository.findDistinctByEventCodeEquals(code);
		if (event != null) {
			int processedFilesCount = photoRepository
					.countByTransmissionDataAndDeletedFalseOrDeletedNull(event.getEventCode());
			event.setProcessedPhotosCount(processedFilesCount);
			eventDetailRepository.save(event);
		}
	}
	
	public void setProcessedFilesCount() {
		try {
			Iterable<EventDetail> eventList = eventDetailRepository.findAll();
			eventList.forEach(event -> {
				int processedFilesCount = photoRepository
						.countByTransmissionDataAndDeletedFalseOrDeletedNull(event.getEventCode());
				event.setProcessedPhotosCount(processedFilesCount);
				eventDetailRepository.save(event);
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

    
    public Page<EventDetail> search(int pageNumber, int pageSize, String searchCriterion) {
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        return eventDetailRepository.findEventsByNameContainsOrEventCodeContainsOrderByIdDesc(searchCriterion, searchCriterion, pageRequest);
	}

    public EventDetail create(EventDetail event) throws ValidationException {
        if (isEmptyString(event.getName())) {
            throw new ValidationException("Event name can't be empty");
        } else if (isEmptyString(event.getEventCode())) {
            throw new ValidationException("Event code can't be empty");
        } else if (event.getDestination() == null) {
            throw new ValidationException("Invalid destination for event");
        }
        
    	int processedFilesCount = photoRepository
				.countByTransmissionDataAndDeletedFalseOrDeletedNull(event.getEventCode());
		event.setProcessedPhotosCount(processedFilesCount);
		
        return this.eventDetailRepository.save(event);
    }

    public EventDetail createXmp(EventDetail event) throws ValidationException {
        if (isEmptyString(event.getName())) {
            throw new ValidationException("Event name can't be empty");
        } else if (event.getDestination() == null) {
            throw new ValidationException("Invalid destination for event");
        }
        
        int processedFilesCount = photoRepository
				.countByTransmissionDataAndDeletedFalseOrDeletedNull(event.getEventCode());
		event.setProcessedPhotosCount(processedFilesCount);
        return this.eventDetailRepository.save(event);
    }
    public  EventDetail getEventById(Integer id){
        return  this.eventDetailRepository.findOne(id);
    }
    public Boolean delete(Integer id) {

        eventDetailRepository.delete(id);
        return true;
    }
    public Integer countByHeadlineAndPhotographer(String headline, String photographer, Long eventDate){
        return eventDetailRepository.countByHeadlineIgnoreCaseAndCreatorIgnoreCaseAndEventDate(headline, photographer, eventDate);
    }
    public String createXmpFile(EventDetail event, JsonNode node) throws IOException {
        String fileName = event.getName() + ".xmp";
        File file = new File(fileName);
        FileWriter fw = null;
        BufferedWriter bw = null;

        try {
            file.createNewFile();
            fw = new FileWriter(file.getAbsoluteFile());
            bw = new BufferedWriter(fw);
            bw.write(prepareContent(event, node));
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                bw.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
        }
        return fileName;
    }

    private String prepareContent(EventDetail event, JsonNode node) {
        String []subject = event.getSubject().split(";");
        String xmpSubject = "";
        for(String sub : subject) {
            if(sub != ""){
                xmpSubject += "\t <rdf:li>" + sub.trim() + "</rdf:li>\n";
            }
        }
        return "<x:xmpmeta xmlns:x=\"adobe:ns:meta/\" x:xmptk=\"XMP Core 5.1.2\">\n" +
                " <rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
                "  <rdf:Description rdf:about=\"\"\n" +
                "    xmlns:photoshop=\"http://ns.adobe.com/photoshop/1.0/\"\n" +
                "    xmlns:xmpRights=\"http://ns.adobe.com/xap/1.0/rights/\"\n" +
                "    xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n" +
                "    xmlns:xmp=\"http://ns.adobe.com/xap/1.0/\"\n" +
                "    xmlns:Iptc4xmpCore=\"http://iptc.org/std/Iptc4xmpCore/1.0/xmlns/\"\n" +
                "    xmlns:Iptc4xmpExt=\"http://iptc.org/std/Iptc4xmpExt/2008-02-29/\"\n" +
                "    xmlns:photomechanic=\"http://ns.camerabits.com/photomechanic/1.0/\"\n" +
                "   photoshop:City=\"" + event.getCity() + "\"\n" +
                "   photoshop:State=\"" + event.getState() + "\"\n" +
                "   photoshop:Country=\"" + event.getCountry() + "\"\n" +
                "   photoshop:Category=\"" + event.getCategory() + "\"\n" +
                "   photoshop:AuthorsPosition=\"" + event.getAuthorsPosition() + "\"\n" +
                "   photoshop:Credit=\"" + event.getCredit() + "\"\n" +
                "   photoshop:Source=\"" + event.getSource() + "\"\n" +
                "   photoshop:CaptionWriter=\"" + event.getCaptionWriter() + "\"\n" +
                "   photoshop:Headline=\"" + event.getHeadline() + "\"\n" +
                "   photoshop:Instructions=\"" + event.getInstructions() + "\"\n" +
                "   photoshop:DateCreated=\"" + node.findPath("eventDate").asText() + "\"\n" +
                "   photoshop:TransmissionReference=\"" + event.getEventCode() + "\"\n" +
                "   xmpRights:Marked=\"True\"\n" +
                "   xmpRights:WebStatement=\"" + event.getWebStatement() + "\"\n" +
                "   xmp:CreateDate=\"" + node.findPath("eventDate").asText() + "\"\n" +
                "   Iptc4xmpCore:CountryCode=\"" + event.getCountryCode() + "\"\n" +
                "   Iptc4xmpCore:Location=\"" + event.getLocation() + "\"\n" +
                "   photomechanic:ColorClassEval=\"0\"\n" +
                "   photomechanic:ColorClassApply=\"False\"\n" +
                "   photomechanic:RatingEval=\"0\"\n" +
                "   photomechanic:RatingApply=\"False\"\n" +
                "   photomechanic:TagEval=\"0\"\n" +
                "   photomechanic:TagApply=\"False\"\n" +
                "   photomechanic:CaptionMergeStyle=\"1\"\n" +
                "   photomechanic:ApplyAPCustom=\"0\"\n" +
                "   photomechanic:MergeAPCustom=\"0\"\n" +
                "   photomechanic:ApplyDateType=\"2\">\n" +
                "   <photoshop:SupplementalCategories>\n" +
                "    <rdf:Bag>\n" +
                "     <rdf:li>" + event.getSubcat() + "</rdf:li>\n" +
                "     <rdf:li>" + event.getSubcat1() + "</rdf:li>\n" +
                "     <rdf:li>" + event.getSubcat2() + "</rdf:li>\n" +
                "    </rdf:Bag>\n" +
                "   </photoshop:SupplementalCategories>\n" +
                "   <xmpRights:UsageTerms>\n" +
                "    <rdf:Alt>\n" +
                "     <rdf:li xml:lang=\"x-default\">" + event.getUsageTerms() + "</rdf:li>\n" +
                "    </rdf:Alt>\n" +
                "   </xmpRights:UsageTerms>\n" +
                "   <dc:subject>\n" +
                "    <rdf:Bag>\n" +
                        xmpSubject +
                "    </rdf:Bag>\n" +
                "   </dc:subject>\n" +
                "   <dc:description>\n" +
                "    <rdf:Alt>\n" +
                "     <rdf:li xml:lang=\"x-default\">" + event.getDescription() + "</rdf:li>\n" +
                "    </rdf:Alt>\n" +
                "   </dc:description>\n" +
                "   <dc:creator>\n" +
                "    <rdf:Seq>\n" +
                "     <rdf:li>" + event.getCreator() + "</rdf:li>\n" +
                "    </rdf:Seq>\n" +
                "   </dc:creator>\n" +
                "   <dc:title>\n" +
                "    <rdf:Alt>\n" +
                "     <rdf:li xml:lang=\"x-default\">" + event.getTitle() + "</rdf:li>\n" +
                "    </rdf:Alt>\n" +
                "   </dc:title>\n" +
                "   <dc:rights>\n" +
                "    <rdf:Alt>\n" +
                "     <rdf:li xml:lang=\"x-default\">" + event.getRights() + "</rdf:li>  \n" +
                "    </rdf:Alt>\n" +
                "   </dc:rights>\n" +
                "   <Iptc4xmpCore:SubjectCode>\n" +
                "    <rdf:Bag>\n" +
                "     <rdf:li>" + event.getSubjectCode() + "</rdf:li>\n" +
                "    </rdf:Bag>\n" +
                "   </Iptc4xmpCore:SubjectCode>\n" +
                "   <Iptc4xmpCore:CreatorContactInfo\n" +
                "    Iptc4xmpCore:CiAdrExtadr=\"" + event.getCiAdrExtadr() + "\"\n" +
                "    Iptc4xmpCore:CiAdrCity=\"" + event.getCiAdrCity() + "\"\n" +
                "    Iptc4xmpCore:CiAdrRegion=\"" + event.getCiAdrRegion() + "\"\n" +
                "    Iptc4xmpCore:CiAdrPcode=\"" + event.getCiAdrPcode() + "\"\n" +
                "    Iptc4xmpCore:CiAdrCtry=\"" + event.getCiAdrCtry() + "\"\n" +
                "    Iptc4xmpCore:CiTelWork=\"" + event.getCiTelWork() + "\"\n" +
                "    Iptc4xmpCore:CiEmailWork=\"" + event.getCiEmailWork() + "\"\n" +
                "    Iptc4xmpCore:CiUrlWork=\"" + event.getCiUrlWork() + "\"/>\n" +
                "   <Iptc4xmpExt:PersonInImage>\n" +
                "    <rdf:Bag>\n" +
                "     <rdf:li/>\n" +
                "    </rdf:Bag>\n" +
                "   </Iptc4xmpExt:PersonInImage>\n" +
                "   <Iptc4xmpExt:LocationCreated>\n" +
                "    <rdf:Bag>\n" +
                "     <rdf:li\n" +
                "      Iptc4xmpExt:Sublocation=\"\"\n" +
                "      Iptc4xmpExt:City=\"\"\n" +
                "      Iptc4xmpExt:ProvinceState=\"\"\n" +
                "      Iptc4xmpExt:CountryName=\"\"\n" +
                "      Iptc4xmpExt:CountryCode=\"\"\n" +
                "      Iptc4xmpExt:WorldRegion=\"\"/>\n" +
                "    </rdf:Bag>\n" +
                "   </Iptc4xmpExt:LocationCreated>\n" +
                "   <Iptc4xmpExt:Event>\n" +
                "    <rdf:Alt>\n" +
                "     <rdf:li xml:lang=\"x-default\">" + event.getName() + "</rdf:li>\n" +
                "    </rdf:Alt>\n" +
                "   </Iptc4xmpExt:Event>" +
                "   <photomechanic:FieldsToApply>\n" +
                "    <rdf:Bag>\n" +
                "     <rdf:li>0x00</rdf:li>\n" +
                "     <rdf:li>0x237</rdf:li>\n" +
                "     <rdf:li>0x25a</rdf:li>\n" +
                "     <rdf:li>0x25f</rdf:li>\n" +
                "     <rdf:li>0x265</rdf:li>\n" +
                "     <rdf:li>0x264</rdf:li>\n" +
                "     <rdf:li>0x205</rdf:li>\n" +
                "     <rdf:li>0x20f</rdf:li>\n" +
                "     <rdf:li>0x1214</rdf:li>\n" +
                "     <rdf:li>0x2214</rdf:li>\n" +
                "     <rdf:li>0x3214</rdf:li>\n" +
                "     <rdf:li>0x219</rdf:li>\n" +
                "     <rdf:li>0x250</rdf:li>\n" +
                "     <rdf:li>0x255</rdf:li>\n" +
                "     <rdf:li>0x26e</rdf:li>\n" +
                "     <rdf:li>0x273</rdf:li>\n" +
                "     <rdf:li>0x27a</rdf:li>\n" +
                "     <rdf:li>0x278</rdf:li>\n" +
                "     <rdf:li>0x269</rdf:li>\n" +
                "     <rdf:li>0x228</rdf:li>\n" +
                "     <rdf:li>0x267</rdf:li>\n" +
                "     <rdf:li>0x20a</rdf:li>\n" +
                "     <rdf:li>0x274</rdf:li>\n" +
                "     <rdf:li>0x25c</rdf:li>\n" +
                "     <rdf:li>0x8002</rdf:li>\n" +
                "     <rdf:li>0x8004</rdf:li>\n" +
                "     <rdf:li>0x8005</rdf:li>\n" +
                "     <rdf:li>0x8006</rdf:li>\n" +
                "     <rdf:li>0x8007</rdf:li>\n" +
                "     <rdf:li>0x8008</rdf:li>\n" +
                "     <rdf:li>0x8009</rdf:li>\n" +
                "     <rdf:li>0x800a</rdf:li>\n" +
                "     <rdf:li>0x800b</rdf:li>\n" +
                "     <rdf:li>0x800c</rdf:li>\n" +
                "     <rdf:li>0x800d</rdf:li>\n" +
                "     <rdf:li>0x8013</rdf:li>\n" +
                "    </rdf:Bag>\n" +
                "   </photomechanic:FieldsToApply>\n" +
                "   <photomechanic:FieldsToMerge>\n" +
                "    <rdf:Bag>\n" +
                "     <rdf:li>0x00</rdf:li>\n" +
                "    </rdf:Bag>\n" +
                "   </photomechanic:FieldsToMerge>\n" +
                "  </rdf:Description>\n" +
                " </rdf:RDF>\n" +
                "</x:xmpmeta>";
    }
}
