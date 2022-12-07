package com.cwidanage.photoftp.repository;

import com.cwidanage.photoftp.models.Photo;
import com.cwidanage.photoftp.models.Queue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author Chathura Widanage
 */
public interface QueueRepository extends PagingAndSortingRepository<Queue, Integer> {
    List<Queue> findByProcessedFalseOrderByDateAddedDesc();

    List<Queue> findByPhotoOrderByDateAddedDesc(Photo photo);
    
    Page<Queue> findAllByPhoto_DeletedFalseOrPhoto_DeletedNullOrderByDateAddedDesc(Pageable pageable);

    Page<Queue> findByProcessedTrueOrderByDateAddedDesc(Pageable pageable);

    Page<Queue> findByProcessedFalseOrderByDateAddedDesc(Pageable pageable);
    
}
