package com.cwidanage.photoftp.controllers;

import com.cwidanage.photoftp.services.PhotoService;

import antlr.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Chathura Widanage
 */
@RestController
@RequestMapping("api/photo")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @RequestMapping("{code}")
    public ResponseEntity get(@PathVariable("code") String code) {
        System.out.println(code);
        return ResponseEntity.ok(this.photoService.getByEventCode(code));
    }
    
    @RequestMapping("/week")
    public ResponseEntity getWeek() {
        return ResponseEntity.ok(this.photoService.getLastWeekCount(7));
    }
    @RequestMapping("/hour")
    public ResponseEntity gethour() {
        return ResponseEntity.ok(this.photoService.getLastWeekCount(0));
    }
    @RequestMapping("/day")
    public ResponseEntity getday() {
        return ResponseEntity.ok(this.photoService.getLastWeekCount(1));
    }
    @RequestMapping("/all")
    public ResponseEntity getallk() {
        return ResponseEntity.ok(this.photoService.getLastWeekCount(365));
    }

	@RequestMapping(value = "/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity searchPhotos(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "pageSize", defaultValue = "20") int pageSize, @RequestParam("searchCriterion") String searchCriterion) {
		return ResponseEntity.ok(this.photoService.search(page, pageSize, searchCriterion));
	}
	
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public ResponseEntity delete(@PathVariable int id) {
	    return ResponseEntity.ok(photoService.delete(id));
	}
	
}
