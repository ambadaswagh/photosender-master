package com.cwidanage.photoftp.repository;

import com.cwidanage.photoftp.models.FTPGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author Chathura Widanage
 */
public interface FtpGroupRepository extends PagingAndSortingRepository<FTPGroup, Integer> {
    Page<FTPGroup> findAllByDeletedFalse(Pageable pageable);
}
