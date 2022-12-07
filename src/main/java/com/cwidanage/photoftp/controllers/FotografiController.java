package com.cwidanage.photoftp.controllers;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cwidanage.photoftp.exceptions.ValidationException;
import com.cwidanage.photoftp.models.Fotografi;
import com.cwidanage.photoftp.resources.ErrorResponse;
import com.cwidanage.photoftp.services.FotografiService;

@RestController
@RequestMapping("api/Fotografi")
public class FotografiController {

	@Autowired
	private FotografiService fotografiService;
	
	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity all(@RequestParam(name = "page", defaultValue = "0") int page, HttpServletRequest request) {
        Fotografi user = (Fotografi)request.getAttribute("USER_ATTRIBUTE");
    	if(user.getAuthorsPosition().equals("Contributer")) {
    		user.setepassword(null);
    		return ResponseEntity.ok().body(new PageImpl<Fotografi>(Arrays.asList(user), new PageRequest(1, 1), 1L));
    	}else {
    		return ResponseEntity.ok().body(this.fotografiService.list(page, 25));
    	}		
	}

	@RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	 public ResponseEntity create(@RequestBody Fotografi fotografi){
		try {
            return ResponseEntity.ok(this.fotografiService.create(fotografi));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
	}

	@RequestMapping("{id}")
	public ResponseEntity getFotografiById(@PathVariable int id) {
		Fotografi fotografi = this.fotografiService.getFotografiById(id);
		if (fotografi == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(fotografi);
	}
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public ResponseEntity delete(@PathVariable int id) {
		return ResponseEntity.ok(fotografiService.delete(id));
	}
}
