package com.cwidanage.photoftp.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import com.cwidanage.photoftp.models.Competition;

import java.util.List;

public interface CompetitionRepository extends PagingAndSortingRepository<Competition, Integer> {
   Competition findOneByid(Integer id);
   List<Competition> findBySubjectIgnoreCase(String subject);
}
