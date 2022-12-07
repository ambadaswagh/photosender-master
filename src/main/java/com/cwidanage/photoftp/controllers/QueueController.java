package com.cwidanage.photoftp.controllers;

import com.cwidanage.photoftp.exceptions.ValidationException;
import com.cwidanage.photoftp.services.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Chathura Widanage
 */
@RestController
@RequestMapping("api/queue")
public class QueueController {

    @Autowired
    private QueueService queueService;

    @RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity list(@RequestParam(name = "page", defaultValue = "0") int page,
                               @RequestParam(name = "p", defaultValue = "true") boolean processed,
                               @RequestParam(name = "np", defaultValue = "true") boolean notProcessed) {
        return ResponseEntity.ok().body(queueService.list(page, 25, processed, notProcessed));
    }

    @RequestMapping(value = "{photoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity list(@PathVariable int photoId) {
        try {
            return ResponseEntity.ok().body(queueService.getByPhotoId(photoId));
        } catch (ValidationException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
