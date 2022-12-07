package com.cwidanage.photoftp.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.cwidanage.photoftp.models.Photo;
import org.springframework.data.repository.query.Param;

/**
 * @author Chathura Widanage
 */
public interface PhotoRepository extends PagingAndSortingRepository<Photo, Integer> {
    
	Photo findDistinctByFileName(String fileName);
	@Query("select p from Photo p where p.transmissionData = :code and (p.deleted = false OR p.deleted IS NULL) Order By p.dateAdded desc")
    List<Photo> findAllByTransmissionDataAndDeletedNullOrDeletedFalseOrderByDateAddedDesc(@Param("code") String code);
	@Query("select p from Photo p where p.id = :id and (p.deleted = false OR p.deleted IS NULL)")
	Photo findById(@Param("id") Integer id);
	
	List<Photo> findAllByTransmissionDataOrderByDateAddedDesc(String code);

    Integer countByIdAndQueue_ProcessedIsTrue(Integer id);
    @Query("select count(p) from Photo p where p.transmissionData = :code and (p.deleted = false OR p.deleted IS NULL)")
    Integer countByTransmissionDataAndDeletedFalseOrDeletedNull(@Param("code")String code);

    Integer countByTransmissionDataAndErrorFalse(String eventCode);
    
    Photo findFirstByTransmissionDataOrderByIdDesc(String eventCode);
    
    @Query("select count(p) from Photo p where p.dateAdded > ?1")
    Long findAllByLastWeek(Date dateFrom);
    @Query("select count(p) from Photo p ")
    Long findAllByLast();

    List<Photo> findPhotosByDescriptionContainsOrJobReferenceContains(String description, String jobReference);
    
    Page<Photo> findPhotosByDescriptionContainsOrJobReferenceContains(String description, String jobReference,  Pageable pageable);

	Page<Photo> findPhotosByDescriptionContainsOrJobReferenceContainsOrFileNameContains(String description, String jobReference, String fileName, Pageable pageable);

}
