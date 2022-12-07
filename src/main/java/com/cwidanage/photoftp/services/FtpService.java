package com.cwidanage.photoftp.services;

import com.cwidanage.photoftp.exceptions.ValidationException;
import com.cwidanage.photoftp.models.FTPAccount;
import com.cwidanage.photoftp.repository.FtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.stereotype.Service;


import java.util.List;

/**
 * @author Chathura Widanage
 */
@Service
public class FtpService extends AbstractService {
    @Autowired
    private FtpRepository ftpRepository;

    @Autowired
    private FileSenderService fileSenderService;

    public Page<FTPAccount> list(int pageNumber, int pageSize) {
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        Page<FTPAccount> ftplist=ftpRepository.findAllByEmailNullAndDeletedFalseOrDeletedNull(pageRequest);
        
        return ftplist; 
    }
    public Page<FTPAccount> listEmail(int pageNumber, int pageSize) {
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        Page<FTPAccount> ftplist=ftpRepository.findAllByEmailNotNullAndDeletedFalseOrDeletedNull(pageRequest);
        return ftplist; 
    }

    public List<FTPAccount> findBy(String query) {
        return ftpRepository.findByDeletedFalseAndNameIgnoreCaseContaining(query);
    }

    public FTPAccount create(FTPAccount ftpAccount) throws ValidationException {
    	
        if (isEmptyString(ftpAccount.getName())) {
            throw new ValidationException("name can't be empty");
        }
        if(ftpAccount.getEmail()!=null) {
            ftpAccount.setHost("email");
            ftpAccount.setUsername("email");
            ftpAccount.setPassword("email");
        }

        return this.ftpRepository.save(ftpAccount);
    }

    public FTPAccount getFtpAccount(int id) {
        return this.ftpRepository.findOne(id);
    }

    public FTPAccount delete(int ftpId) {
        FTPAccount ftpAccount = this.ftpRepository.findOne(ftpId);
        ftpAccount.setDeleted(true);
        return ftpRepository.save(ftpAccount);
    }

    public boolean test(int ftpId){
        FTPAccount ftpAccount = this.getFtpAccount(ftpId);
        SessionFactory defaultFtpSessionFactory = fileSenderService.getDefaultFtpSessionFactory(ftpAccount);
        return defaultFtpSessionFactory.getSession().isOpen();
    }
}
