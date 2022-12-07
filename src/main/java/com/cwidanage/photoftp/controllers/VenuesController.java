package com.cwidanage.photoftp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cwidanage.photoftp.exceptions.ValidationException;
import com.cwidanage.photoftp.models.Venues;
import com.cwidanage.photoftp.resources.ErrorResponse;
import com.cwidanage.photoftp.services.VenuesService;

@RestController
@RequestMapping("api/Venues")
public class VenuesController {

	@Autowired
	private VenuesService venuesService;
	
	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity all(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "pageSize", defaultValue = "15") int pageSize) {
		return ResponseEntity.ok().body(this.venuesService.list(page, pageSize));
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	 public ResponseEntity create(@RequestBody Venues venues){
		try {
            return ResponseEntity.ok(this.venuesService.create(venues));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
	}

	@RequestMapping("{id}")
	public ResponseEntity getVenuesById(@PathVariable int id) {
		Venues venues = this.venuesService.getVenuesById(id);
		if (venues == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(venues);
	}
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public ResponseEntity delete(@PathVariable int id) {
		return ResponseEntity.ok(venuesService.delete(id));
	}
}
