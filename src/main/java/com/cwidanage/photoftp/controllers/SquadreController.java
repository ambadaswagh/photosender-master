package com.cwidanage.photoftp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cwidanage.photoftp.exceptions.ValidationException;
import com.cwidanage.photoftp.models.Squadre;
import com.cwidanage.photoftp.resources.ErrorResponse;
import com.cwidanage.photoftp.services.SquadreService;

@RestController
@RequestMapping("api/Squadre")
public class SquadreController {

	@Autowired
	private SquadreService squadreService;
	
	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity all(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "pageSize", defaultValue = "15") int pageSize) {
		return ResponseEntity.ok().body(this.squadreService.list(page, pageSize));
	}

	@RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	 public ResponseEntity create(@RequestBody Squadre squadre){
		try {
            return ResponseEntity.ok(this.squadreService.create(squadre));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
	}

	@RequestMapping("{id}")
	public ResponseEntity getSquadreById(@PathVariable int id) {
		Squadre squadre = this.squadreService.getSquadreById(id);
		if (squadre == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(squadre);
	}
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public ResponseEntity delete(@PathVariable int id) {
		return ResponseEntity.ok(squadreService.delete(id));
	}
}
