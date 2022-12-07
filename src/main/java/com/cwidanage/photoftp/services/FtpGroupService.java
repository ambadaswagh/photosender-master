package com.cwidanage.photoftp.services;

import com.cwidanage.photoftp.models.FTPGroup;
import com.cwidanage.photoftp.repository.FtpGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * @author Chathura Widanage
 */
@Service
public class FtpGroupService extends AbstractService {
    @Autowired
    private FtpGroupRepository repo;

    public Page<FTPGroup> all(int page, int pageSize) {
        PageRequest pageRequest = new PageRequest(page, pageSize);
        return repo.findAllByDeletedFalse(pageRequest);
    }

    public FTPGroup create(FTPGroup ftpGroup) {
        return this.repo.save(ftpGroup);
    }

    public FTPGroup markDelete(int groupId) {
        FTPGroup ftpGroup = repo.findOne(groupId);
        ftpGroup.setDeleted(true);
        return repo.save(ftpGroup);
    }

    public FTPGroup get(int groupId) {
        return this.repo.findOne(groupId);
    }

}
