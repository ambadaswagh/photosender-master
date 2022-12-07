package com.cwidanage.photoftp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.cwidanage.photoftp.exceptions.ValidationException;
import com.cwidanage.photoftp.models.Squadre;
import com.cwidanage.photoftp.repository.SquadreRepository;

@Service
public class SquadreService extends AbstractService {

	@Autowired
	private SquadreRepository squadreRepository;
	
	public Page<Squadre> list(int pageNumber, int pageSize) {
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize,Sort.Direction.ASC,"nomeit");
        return squadreRepository.findAll(pageRequest);
	}
	
	public Squadre create(Squadre squadre) throws ValidationException {
		return this.squadreRepository.save(squadre);
	}
	public  Squadre getSquadreById(Integer id){
		return  this.squadreRepository.findOne(id);
	}
	public Boolean delete(Integer id) {

		 squadreRepository.delete(id);
		return true;
	}
}




