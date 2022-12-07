package com.cwidanage.photoftp.controllers;

import antlr.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cwidanage.photoftp.exceptions.ValidationException;
import com.cwidanage.photoftp.models.Competition;
import com.cwidanage.photoftp.models.CompetitionModel;
import com.cwidanage.photoftp.resources.ErrorResponse;
import com.cwidanage.photoftp.services.CompetitionService;

@RestController
@RequestMapping("api/Competition")
public class CompetitionController {

	@Autowired
	private CompetitionService competitionService;
	
	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity all(@RequestParam(name = "page", defaultValue = "0") int page) {
		return ResponseEntity.ok().body(this.competitionService.list(page, 25));
	}
	@RequestMapping(value = "/{id}/squadre", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity allSquadre(@PathVariable int id) {
		return ResponseEntity.ok().body(this.competitionService.getCompetitionById(id).getSquadreList());
	}

	@RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	 public ResponseEntity create(@RequestBody CompetitionModel competition){
		try {
            return ResponseEntity.ok(this.competitionService.create(competition));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
	}

	@RequestMapping("{id}")
	public ResponseEntity getCompetitionById(@PathVariable int id) {
		Competition competition = this.competitionService.getCompetitionById(id);
		if (competition == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(competition);
	}
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public ResponseEntity delete(@PathVariable int id) {
		return ResponseEntity.ok(competitionService.delete(id));
	}

	@RequestMapping(value="/getBySubject", method = RequestMethod.GET)
	public ResponseEntity getCompetitionBySubject(@RequestParam(name = "subject") String subject){
		return ResponseEntity.ok(this.competitionService.getCompetitionBySubject(subject));
	}
}
