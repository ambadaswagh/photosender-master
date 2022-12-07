package com.cwidanage.photoftp.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import com.cwidanage.photoftp.models.Copyright;

public interface CopyrightRepository extends PagingAndSortingRepository<Copyright, Integer> {
   Copyright findOneByid(Integer id);
}
