package com.cwidanage.photoftp.repository;

import com.cwidanage.photoftp.models.FTPAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author Chathura Widanage
 */
public interface FtpRepository extends PagingAndSortingRepository<FTPAccount, Integer> {
    List<FTPAccount> findByDeletedFalseAndNameIgnoreCaseContaining(String name);

    Page<FTPAccount> findAllByDeletedFalseOrDeletedNull(Pageable pageable);
    Page<FTPAccount> findAllByEmailNullAndDeletedFalseOrDeletedNull(Pageable pageable);
    Page<FTPAccount> findAllByEmailNotNullAndDeletedFalseOrDeletedNull(Pageable pageable);
}
