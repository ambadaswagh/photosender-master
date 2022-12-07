package com.cwidanage.photoftp.controllers;

import com.cwidanage.photoftp.exceptions.ValidationException;
import com.cwidanage.photoftp.models.*;
import com.cwidanage.photoftp.resources.ErrorResponse;
import com.cwidanage.photoftp.services.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Chathura Widanage
 */
@RestController
@RequestMapping("api/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private AgencyService agencyService;

    @Autowired
    private FotografiService fotografiService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private VenuesService venuesService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PhotoService photoService;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity all(@RequestParam(name = "page", defaultValue = "0") int page) {
        Page<EventDetail> allEvents = this.eventService.list(page, 15);
        List<EventDetail> eventsList = allEvents.getContent();
        return ResponseEntity.ok().body(allEvents);
    }
    
    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity searchPhotos(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "pageSize", defaultValue = "15") int pageSize, @RequestParam("searchCriterion") String searchCriterion) {
		return ResponseEntity.ok(this.eventService.search(page, pageSize, searchCriterion));
	}

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@RequestBody EventDetail event) {
        try {
            if(event.getId() == null){
                List<Agenzia> agenziaList = agencyService.getAgencyByName(event.getInstructions());
                if(!agenziaList.isEmpty()){
                    Agenzia agenzia = agenziaList.get(0);
                    event.setCiAdrExtadr(agenzia.getIndirizzo());
                    event.setCiAdrCity(agenzia.getProvincia());
                    event.setCiAdrRegion(agenzia.getCitta());
                    event.setCiAdrPcode(agenzia.getCap());
                    event.setCiAdrCtry(agenzia.getNazione());
                    event.setCiTelWork(agenzia.getTelefono());
                    event.setCiEmailWork(agenzia.getEmail());
                    event.setCiUrlWork(agenzia.getSito());
                }
            }
            return ResponseEntity.ok(this.eventService.create(event));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @RequestMapping(value = "/createXmpGenerator", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void createXmpGenerator(@RequestBody JsonNode node,
                                             HttpServletResponse response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            EventDetail event = new EventDetail();
            FTPGroup dest = mapper.treeToValue(node.path("destination"), FTPGroup.class);
            populateAgenzia(event, node);
            populateFotografy(event, node);
            populateSubject(event, node);
            populateVenue(event, node);

            //event.setEventCode(node.findPath("eventCode").asText());
            event.setDestination(dest);
            event.setDescription(node.findPath("description").asText());
            event.setCredit(node.findPath("credit").asText());
            event.setHeadline(node.findPath("headline").asText());
            event.setName(event.getHeadline());
            event.setRights(node.findPath("copyright").asText());
            event.setUsageTerms(node.findPath("newInstructions").asText());
            event.setTitle(node.findPath("title").asText());
            event.setInstructions(node.findPath("newInstructions").asText());
            event.setSubject(node.findPath("keywords").asText());
            event.setfilename(node.findPath("filename").asText());
            event.setProcessedPhotosCount(0);
            String eventDateString = node.findPath("eventDate").asText();

            if(eventDateString != ""){
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
                dateFormat.setTimeZone(TimeZone.getDefault());
                Date eventDate = dateFormat.parse(eventDateString);
                event.setEventDate(eventDate.getTime());
            }
            if(this.eventService.countByHeadlineAndPhotographer(event.getHeadline(), event.getCreator(), event.getEventDate()) > 0){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else
                {
                EventDetail createdEvent = this.eventService.createXmp(event);
                createdEvent.setEventCode("SP24-" + createdEvent.getId());
                EventDetail updatedEvent = this.eventService.createXmp(createdEvent);
                String createdXmpfile = this.eventService.createXmpFile(updatedEvent, node);
                File createdXmpFile = new File(createdXmpfile);
                
                //copy xmp file 
                String xmpPath="PhotoFtp/xmpFiles";
                FileUtils.forceMkdir(new File(xmpPath));
                FileUtils.copyFile(createdXmpFile, new File(xmpPath+"/"+node.findPath("filename").asText()+".xmp"));
                
                InputStream targetStream = new FileInputStream(createdXmpFile);
                response.addHeader("Content-disposition", "attachment;filename=event.xmp");
                response.setContentType("text/xml");
                org.apache.commons.io.IOUtils.copy(targetStream, response.getOutputStream());
                response.flushBuffer();
                targetStream.close();
                response.setStatus(HttpServletResponse.SC_OK);
                if(node.findPath("emailCheck").asBoolean()){
                    emailService.sendMessageWithAttachment(event, createdXmpfile);
                } else {
                    createdXmpFile.delete();
                }
            }



        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }
    @RequestMapping(value = "/getxmp/{eventID}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void downloadXmpFile(@PathVariable Integer eventID,
                                             HttpServletResponse response) {
        try {
        	
        	EventDetail event=eventService.getEventById(eventID);
        	if(event!=null) {
        		String xmpPath="PhotoFtp/xmpFiles";
        		File xmpFile=new File(xmpPath+"/"+event.getfilename()+".xmp");
        		InputStream targetStream = new FileInputStream(xmpFile);
                response.addHeader("Content-disposition", "attachment;filename=event.xmp");
                response.setContentType("text/xml");
                org.apache.commons.io.IOUtils.copy(targetStream, response.getOutputStream());
                response.flushBuffer();
                targetStream.close();
                response.setStatus(HttpServletResponse.SC_OK);
        	}
        	


        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }
    @RequestMapping("{id}")
    public ResponseEntity getEventById(@PathVariable int id) {
        EventDetail event = this.eventService.getEventById(id);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(event);
    }
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable int id) {
        return ResponseEntity.ok(eventService.delete(id));
    }

    private void populateAgenzia(EventDetail event, JsonNode node){
        JsonNode agenziaNode = node.path("source");
        if(agenziaNode != null){
            Agenzia agenzia = this.agencyService.getAgencyById(agenziaNode.path("id").asInt());
            if(agenzia != null){
                event.setCiAdrExtadr(agenzia.getIndirizzo());
                event.setCiAdrCity(agenzia.getProvincia());
                event.setCiAdrRegion(agenzia.getCitta());
                event.setCiAdrPcode(agenzia.getCap());
                event.setCiAdrCtry(agenzia.getNazione());
                event.setCiTelWork(agenzia.getTelefono());
                event.setCiEmailWork(agenzia.getEmail());
                event.setCiUrlWork(agenzia.getSito());
                event.setWebStatement(agenzia.getSito());
                event.setSource(agenzia.getNome_agenzia());
            }
        }
    }
    private void populateFotografy(EventDetail event, JsonNode node){
        JsonNode fotografiNode = node.path("creator");
        if(fotografiNode != null){
            Fotografi fotografi = this.fotografiService.getFotografiById(fotografiNode.path("id").asInt());
            if(fotografi != null){
                event.setAuthorsPosition(fotografi.getAuthorsPosition());
                event.setPemail(fotografi.getemail());
                event.setCaptionWriter(fotografi.getIniziali());
                event.setCreator(fotografi.getNome_fotografo());
            }
        }
    }
    private void populateSubject(EventDetail event, JsonNode node){
        JsonNode subjectNode = node.path("subject");
        if(subjectNode != null){
            Subject subject = this.subjectService.getSubjectById(subjectNode.path("id").asInt());
            if(subject != null){
                event.setCategory(subject.getCategoria());
                event.setSubcat(subject.getSubcat());
                event.setSubcat1(subject.getSubcat1());
                event.setSubcat2(subject.getSubcat2());
                event.setSubjectCode(subject.getCodice());
            }
        }
    }
    private void populateVenue(EventDetail event, JsonNode node){
        JsonNode venueNode = node.path("venue");
        if(venueNode != null){
            Venues venue = this.venuesService.getVenuesById(venueNode.path("id").asInt());
            if(venue != null){
                event.setLocation(venue.getVenue_name());
                event.setCountryCode(venue.getIso());
                event.setCity(venue.getCity());
                event.setState(venue.getState());
                event.setCountry(venue.getNation());
            }
        }
    }
}
