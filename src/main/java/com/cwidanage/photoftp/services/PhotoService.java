package com.cwidanage.photoftp.services;

import com.cwidanage.photoftp.models.Photo;
import com.cwidanage.photoftp.repository.EventDetailRepository;
import com.cwidanage.photoftp.repository.PhotoRepository;
import com.cwidanage.photoftp.repository.QueueRepository;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

/**
 * @author Chathura Widanage
 */
@Service
@ConfigurationProperties(prefix = "photoftp.thumb")
public class PhotoService {
   
	private final static Logger logger = LogManager.getLogger(PhotoService.class);
	private String source;
	private String destination;
	
	private File sourceFolder;
	private File thumbsFolder;
	
	@PostConstruct
	public void init() throws IOException {
	   this.sourceFolder = new File(source);
	   this.thumbsFolder = new File(destination);
	   FileUtils.forceMkdir(this.sourceFolder);
	   FileUtils.forceMkdir(this.thumbsFolder);
	}
	    
	@Autowired
    private PhotoRepository photoRepository;
	
	@Autowired
    private EventService eventService;
    
    @Autowired
    private QueueRepository queueRepository;
    
    public Photo getPhotoByName(String filename) {
        return this.photoRepository.findDistinctByFileName(filename);
    }

    public List<Photo> getAllByEventCode(String code) {
        return this.photoRepository.findAllByTransmissionDataOrderByDateAddedDesc(code);
    }
    
    public List<Photo> getByEventCode(String code) {
        return this.photoRepository.findAllByTransmissionDataAndDeletedNullOrDeletedFalseOrderByDateAddedDesc(code);
    }

    public Integer countProcessedPhotos(Integer id) {
        return this.photoRepository.countByIdAndQueue_ProcessedIsTrue(id);
    }
    
    public Integer countProcessedPhotosByCode(String code) {
        return this.photoRepository.countByTransmissionDataAndDeletedFalseOrDeletedNull(code);
    }
    
    public Long getLastWeekCount(int days){
    	long DAY_IN_MS = 1000 * 60 * 60 * 24;
    	long DAY_IN_HOUR=1000 * 60 * 60;
    	if(days==7) {
    		return this.photoRepository.findAllByLastWeek(new Date(System.currentTimeMillis() - (7 * DAY_IN_MS)));	
    	}else if(days==1) {
    		return this.photoRepository.findAllByLastWeek(new Date(System.currentTimeMillis() - (1 * DAY_IN_MS)));
    	}else if(days==0)  {
    		return this.photoRepository.findAllByLastWeek(new Date(System.currentTimeMillis() - (DAY_IN_HOUR)));
    	}else {
    		return this.photoRepository.findAllByLast();
    	}
    	
    	
    }

	public List<Photo> search(String searchCriterion) {

    	return this.photoRepository.findPhotosByDescriptionContainsOrJobReferenceContains(searchCriterion, searchCriterion);

	}
	
	public Page<Photo> search(int pageNumber, int pageSize, String searchCriterion) {
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        return photoRepository.findPhotosByDescriptionContainsOrJobReferenceContainsOrFileNameContains(searchCriterion, searchCriterion, searchCriterion, pageRequest);
	}
	
	public Boolean delete(Integer id) {
		
		Photo photo = photoRepository.findOne(id);
		File thumbsPhoto = new File(thumbsFolder, photo.getFileName());
		File sourcePhoto = new File(sourceFolder, photo.getFileName());
		
		try {
			if (thumbsPhoto.exists()) {
				FileUtils.forceDelete(thumbsPhoto);
			}
			if (sourcePhoto.exists()) {
				FileUtils.forceDelete(sourcePhoto);
			}
		} catch (IOException e) {
			logger.error("Error deleting thumb photo", e);
		}
		
		photo.setDeleted(true);
		photoRepository.save(photo);
		eventService.updateProcessedPhotoCount(photo.getTransmissionData());
		return true;
	}
	
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}
	
}
