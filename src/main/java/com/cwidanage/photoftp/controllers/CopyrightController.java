package com.cwidanage.photoftp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cwidanage.photoftp.exceptions.ValidationException;
import com.cwidanage.photoftp.models.Copyright;
import com.cwidanage.photoftp.resources.ErrorResponse;
import com.cwidanage.photoftp.services.CopyrightService;

@RestController
@RequestMapping("api/Copyright")
public class CopyrightController {

	@Autowired
	private CopyrightService copyrightService;
	
	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity all(@RequestParam(name = "page", defaultValue = "0") int page) {
		return ResponseEntity.ok().body(this.copyrightService.list(page, 25));
	}

	@RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	 public ResponseEntity create(@RequestBody Copyright copyright){
		try {
            return ResponseEntity.ok(this.copyrightService.create(copyright));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
	}

	@RequestMapping("{id}")
	public ResponseEntity getCopyrightById(@PathVariable int id) {
		Copyright copyright = this.copyrightService.getCopyrightById(id);
		if (copyright == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(copyright);
	}
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public ResponseEntity delete(@PathVariable int id) {
		return ResponseEntity.ok(copyrightService.delete(id));
	}
}
