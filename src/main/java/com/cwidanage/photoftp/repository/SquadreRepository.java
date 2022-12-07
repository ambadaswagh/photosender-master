package com.cwidanage.photoftp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.cwidanage.photoftp.models.Squadre;


public interface SquadreRepository extends PagingAndSortingRepository<Squadre, Integer> {
    Squadre findOneByid(Integer id);
    
    @Query("Select s from Squadre s Where s.nomeit=?1")
    Squadre findOneByNomeIt(String nomeit);

}




