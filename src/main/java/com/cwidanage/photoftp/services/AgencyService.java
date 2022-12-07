package com.cwidanage.photoftp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.cwidanage.photoftp.exceptions.ValidationException;
import com.cwidanage.photoftp.models.Agenzia;
import com.cwidanage.photoftp.repository.AgencyRepository;

import java.util.List;

@Service
public class AgencyService extends AbstractService {

	@Autowired
	private AgencyRepository agencyRepository;
	
	public Page<Agenzia> list(int pageNumber, int pageSize) {
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        return agencyRepository.findAll(pageRequest);
	}
	
	public Agenzia create(Agenzia agency) throws ValidationException {
		if (isEmptyString(agency.getNome_agenzia())) {
			throw new ValidationException("Agency name can't be empty");
		} else if (isEmptyString(agency.getEmail())) {
			throw new ValidationException("Agency email can't be empty");
		} else if (isEmptyString(agency.getTelefono())) {
			throw new ValidationException("Agency phone can't be empty");
		}
		return this.agencyRepository.save(agency);
	}
	public  Agenzia getAgencyById(Integer id){
		return  this.agencyRepository.findOne(id);
	}
	public Boolean delete(Integer aid) {

		 agencyRepository.delete(aid);
		return true;
	}
	public List<Agenzia> getAgencyByName(String name){
		return agencyRepository.findFirstByNome_agenziaIgnoreCase(name);
	}
}
