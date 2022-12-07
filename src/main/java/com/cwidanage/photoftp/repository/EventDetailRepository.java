package com.cwidanage.photoftp.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.cwidanage.photoftp.models.EventDetail;
import com.cwidanage.photoftp.models.Photo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;

/**
 * @author Chathura Widanage
 */
public interface EventDetailRepository extends PagingAndSortingRepository<EventDetail, Integer> {
    EventDetail findDistinctByEventCodeEquals(String code);

    EventDetail findOneByid(Integer id);
    
    Page<EventDetail> findAllByOrderByIdDesc(Pageable pgbl);

    Integer countByHeadlineIgnoreCaseAndCreatorIgnoreCaseAndEventDate(String headline, String photographer, Long eventDate);
    
    Page<EventDetail> findEventsByNameContainsOrEventCodeContainsOrderByIdDesc(String name, String code, Pageable pageable);
}

