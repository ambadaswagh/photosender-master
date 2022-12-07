package com.cwidanage.photoftp.services;

import com.cwidanage.photoftp.exceptions.ValidationException;
import com.cwidanage.photoftp.models.Photo;
import com.cwidanage.photoftp.models.Queue;
import com.cwidanage.photoftp.repository.PhotoRepository;
import com.cwidanage.photoftp.repository.QueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Chathura Widanage
 */
@Service
public class QueueService {

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private PhotoRepository photoRepository;

    public Page<Queue> list(int pageNumber, int pageSize, boolean processed, boolean notProcessed) {
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        if (processed && !notProcessed) {
            return queueRepository.findByProcessedTrueOrderByDateAddedDesc(pageRequest);
        } else if (!processed && notProcessed) {
            return queueRepository.findByProcessedFalseOrderByDateAddedDesc(pageRequest);
        }
        return queueRepository.findAllByPhoto_DeletedFalseOrPhoto_DeletedNullOrderByDateAddedDesc(pageRequest);
    }

    public List<Queue> getByPhotoId(int photoId) throws ValidationException {
        Photo photo = photoRepository.findById(photoId);
        if (photo == null) {
            throw new ValidationException("No such photo");
        }
        return this.queueRepository.findByPhotoOrderByDateAddedDesc(photo);
    }
}
