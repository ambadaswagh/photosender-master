package com.cwidanage.photoftp.controllers;

import com.cwidanage.photoftp.services.ThumbnailService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author Chathura Widanage
 */
@RestController
@RequestMapping("api/thumb")
public class ThumbnailController {

    private final static Logger logger = LogManager.getLogger(ThumbnailController.class);

    @Autowired
    private ThumbnailService thumbnailService;

    @ResponseBody
    @RequestMapping("{file:.+}")
    public byte[] thumb(@PathVariable("file") String photoName) {
        try {
            return thumbnailService.getThumbnail(photoName);
        } catch (IOException e) {
            logger.error("Error in receiving thumbnail for {}", photoName, e);
            return new byte[0];
        }
    }
}
