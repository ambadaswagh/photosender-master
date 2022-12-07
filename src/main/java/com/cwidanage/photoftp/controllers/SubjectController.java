package com.cwidanage.photoftp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cwidanage.photoftp.exceptions.ValidationException;
import com.cwidanage.photoftp.models.Subject;
import com.cwidanage.photoftp.resources.ErrorResponse;
import com.cwidanage.photoftp.services.SubjectService;

@RestController
@RequestMapping("api/Subject")
public class SubjectController {

	@Autowired
	private SubjectService subjectService;
	
	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity all(@RequestParam(name = "page", defaultValue = "0") int page) {
		return ResponseEntity.ok().body(this.subjectService.list(page, 25));
	}

	@RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	 public ResponseEntity create(@RequestBody Subject subject){
		try {
            return ResponseEntity.ok(this.subjectService.create(subject));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
	}

	@RequestMapping("{id}")
	public ResponseEntity getSubjectById(@PathVariable int id) {
		Subject subject = this.subjectService.getSubjectById(id);
		if (subject == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(subject);
	}
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public ResponseEntity delete(@PathVariable int id) {
		return ResponseEntity.ok(subjectService.delete(id));
	}
}
