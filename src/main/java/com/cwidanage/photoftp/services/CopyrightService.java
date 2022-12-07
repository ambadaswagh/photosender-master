package com.cwidanage.photoftp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.cwidanage.photoftp.exceptions.ValidationException;
import com.cwidanage.photoftp.models.Copyright;
import com.cwidanage.photoftp.repository.CopyrightRepository;

@Service
public class CopyrightService extends AbstractService {

    @Autowired
    private CopyrightRepository copyrightRepository;

    public Page<Copyright> list(int pageNumber, int pageSize) {
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        return copyrightRepository.findAll(pageRequest);
    }

    public Copyright create(Copyright copyright) throws ValidationException {
        return this.copyrightRepository.save(copyright);
    }
    public  Copyright getCopyrightById(Integer id){
        return  this.copyrightRepository.findOne(id);
    }
    public Boolean delete(Integer id) {

        copyrightRepository.delete(id);
        return true;
    }
}