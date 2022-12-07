package com.cwidanage.photoftp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.cwidanage.photoftp.exceptions.ValidationException;
import com.cwidanage.photoftp.models.Subject;
import com.cwidanage.photoftp.repository.SubjectRepository;

@Service
public class SubjectService extends AbstractService {

	@Autowired
	private SubjectRepository subjectRepository;
	
	public Page<Subject> list(int pageNumber, int pageSize) {
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        return subjectRepository.findAll(pageRequest);
	}
	
	public Subject create(Subject subject) throws ValidationException {
		return this.subjectRepository.save(subject);
	}
	public  Subject getSubjectById(Integer id){
		return  this.subjectRepository.findOne(id);
	}
	public Boolean delete(Integer aid) {

		 subjectRepository.delete(aid);
		return true;
	}
}
