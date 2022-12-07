package com.cwidanage.photoftp.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import com.cwidanage.photoftp.models.Subject;

public interface SubjectRepository extends PagingAndSortingRepository<Subject, Integer> {
   Subject findOneByAid(Integer aid);
}
