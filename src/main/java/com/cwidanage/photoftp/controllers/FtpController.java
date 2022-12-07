package com.cwidanage.photoftp.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cwidanage.photoftp.exceptions.ValidationException;
import com.cwidanage.photoftp.models.FTPAccount;
import com.cwidanage.photoftp.resources.ErrorResponse;
import com.cwidanage.photoftp.resources.SuccessResponse;
import com.cwidanage.photoftp.services.FtpService;
import com.cwidanage.photoftp.util.AES;


/**
 * @author Chathura Widanage
 */
@RestController
@RequestMapping("api/ftp")
public class FtpController {

    private Logger logger = LogManager.getLogger(FtpController.class);

    @Autowired
    private FtpService ftpService;
    
    @Autowired
    private Environment environment;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity list(@RequestParam(name = "page", defaultValue = "0") int page) {
        return ResponseEntity.ok(ftpService.list(page, 25));
    }
    @RequestMapping(value = "/email", method = RequestMethod.GET)
    public ResponseEntity listEmail(@RequestParam(name = "page", defaultValue = "0") int page) {
        return ResponseEntity.ok(ftpService.listEmail(page, 25));
    }

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public ResponseEntity list(@RequestParam(name = "q", defaultValue = "") String query) {
        return ResponseEntity.ok(ftpService.findBy(query));
    }

    @RequestMapping(value = "{ftpId}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable int ftpId) {
        return ResponseEntity.ok(ftpService.delete(ftpId));
    }

    @RequestMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody FTPAccount ftpAccount) {
        try {
        	String encodedPassword = AES.encrypt(ftpAccount.getPassword(), environment.getProperty("secretKey"));
        	ftpAccount.setPassword(encodedPassword);
            return ResponseEntity.ok(ftpService.create(ftpAccount));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @RequestMapping("{id}")
    public ResponseEntity getFtp(@PathVariable int id) {
        FTPAccount ftpAccount = this.ftpService.getFtpAccount(id);
        if (ftpAccount == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ftpAccount);
    }

    @RequestMapping(value = "test/{id}")
    public ResponseEntity test(@PathVariable int id) {
        try {
            boolean testResult = this.ftpService.test(id);
            if (testResult) {
                return ResponseEntity.ok(new SuccessResponse("Working"));
            } else {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
            }
        } catch (Exception e) {
            logger.debug("Connection test failed", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ErrorResponse(e.getCause().getMessage()));
        }
    }
}
