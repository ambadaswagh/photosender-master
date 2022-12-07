package com.cwidanage.photoftp.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import com.cwidanage.photoftp.models.Venues;


public interface VenuesRepository extends PagingAndSortingRepository<Venues, Integer> {
   Venues findOneByid(Integer id);

}
