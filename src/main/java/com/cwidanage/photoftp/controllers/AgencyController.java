package com.cwidanage.photoftp.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cwidanage.photoftp.exceptions.ValidationException;
import com.cwidanage.photoftp.models.Agenzia;
import com.cwidanage.photoftp.resources.ErrorResponse;
import com.cwidanage.photoftp.services.AgencyService;

import javax.validation.ConstraintViolationException;

@RestController
@RequestMapping("api/agency")
public class AgencyController {

	@Autowired
	private AgencyService agencyService;
	
	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity all(@RequestParam(name = "page", defaultValue = "0") int page) {
		
		return ResponseEntity.ok().body(this.agencyService.list(page, 25));
	}

	@RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	 public ResponseEntity create(@RequestBody Agenzia agency){
		try {

            return ResponseEntity.ok(this.agencyService.create(agency));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }catch (ConstraintViolationException e){
			return ResponseEntity.badRequest().body(new ErrorResponse("Enter valid email."));
		}
        catch (Exception e) {
			return ResponseEntity.badRequest().body(new ErrorResponse("Error"));
        }
	}

	@RequestMapping("{id}")
	public ResponseEntity getAgencyById(@PathVariable int id) {
		Agenzia agenzia = this.agencyService.getAgencyById(id);
		if (agenzia == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(agenzia);
	}
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public ResponseEntity delete(@PathVariable int id) {
		return ResponseEntity.ok(agencyService.delete(id));
	}
}
