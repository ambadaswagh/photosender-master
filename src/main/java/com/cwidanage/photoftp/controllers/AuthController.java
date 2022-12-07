package com.cwidanage.photoftp.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cwidanage.photoftp.models.Fotografi;

/**
 * @author Chathura Widanage
 */
@RestController
@RequestMapping("api/auth")
public class AuthController {
    @RequestMapping(value = "ping", method = RequestMethod.GET)
    public ResponseEntity ping() {
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "user", method = RequestMethod.GET)
    public ResponseEntity user(HttpServletRequest request) {
        Fotografi user = (Fotografi)request.getAttribute("USER_ATTRIBUTE");
    	if(user == null) {
        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    	}
    	user.setepassword(null);
    	return ResponseEntity.ok(user);
    }

}
