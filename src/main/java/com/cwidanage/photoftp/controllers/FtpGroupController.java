package com.cwidanage.photoftp.controllers;

import com.cwidanage.photoftp.models.FTPGroup;
import com.cwidanage.photoftp.services.FtpGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Chathura Widanage
 */
@RestController
@RequestMapping("api/group")
public class FtpGroupController {
    @Autowired
    private FtpGroupService service;

    @RequestMapping(path = "", method = RequestMethod.GET)
    public ResponseEntity all(@RequestParam(name = "page", defaultValue = "0") int page,
                              @RequestParam(name = "pageSize", defaultValue = "25") int pageSize) {
        return ResponseEntity.ok(service.all(page, pageSize));
    }

    @RequestMapping(path = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@RequestBody FTPGroup ftpGroup) {
        return ResponseEntity.ok(service.create(ftpGroup));
    }

    @RequestMapping(path = "{ftpGroupId}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity delete(@PathVariable int ftpGroupId) {
        return ResponseEntity.ok(service.markDelete(ftpGroupId));
    }


    @RequestMapping(path = "{ftpGroupId}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity get(@PathVariable int ftpGroupId) {
        FTPGroup ftpGroup = service.get(ftpGroupId);
        if (ftpGroup != null) {
            return ResponseEntity.ok(service.get(ftpGroupId));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
