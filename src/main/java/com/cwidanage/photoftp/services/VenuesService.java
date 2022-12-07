package com.cwidanage.photoftp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.cwidanage.photoftp.exceptions.ValidationException;
import com.cwidanage.photoftp.models.Venues;
import com.cwidanage.photoftp.repository.VenuesRepository;

@Service
public class VenuesService extends AbstractService {

	@Autowired
	private VenuesRepository venuesRepository;
	
	public Page<Venues> list(int pageNumber, int pageSize) {
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize,Sort.Direction.ASC,"venuename");
        return venuesRepository.findAll(pageRequest);
	}
	
	public Venues create(Venues venues) throws ValidationException {
		return this.venuesRepository.save(venues);
	}
	public  Venues getVenuesById(Integer id){
		return  this.venuesRepository.findOne(id);
	}
	public Boolean delete(Integer id) {

		venuesRepository.delete(id);
		return true;
	}
}
