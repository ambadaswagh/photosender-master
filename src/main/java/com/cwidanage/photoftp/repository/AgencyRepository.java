package com.cwidanage.photoftp.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.cwidanage.photoftp.models.Agenzia;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AgencyRepository extends PagingAndSortingRepository<Agenzia, Integer> {
   Agenzia findOneByAid(Integer aid);

   @Query("select a from Agenzia a where lower(nome_agenzia) = lower(:name)")
   List<Agenzia> findFirstByNome_agenziaIgnoreCase(@Param("name")String name);
}
