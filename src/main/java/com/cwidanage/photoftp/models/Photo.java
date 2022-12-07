package com.cwidanage.photoftp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;
import java.util.List;

/**
 * @author Chathura Widanage
 */
@Entity
@Table(name = "photo")
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "FILE_NAME", nullable = false)
    private String fileName;
    
    @Column(name = "ORIGINAL_FILE_NAME", nullable = false)
    private String originalFileName;

    @Column(name = "DATE_ADDED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAdded;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Queue> queue;

    @Column(name = "transmission_data")
    private String transmissionData;

    @Column(name = "description", length = 500)
    String description;

    @Column(name = "headline", length = 500)
    String headline;

    @Column(name = "LOG")
    private String log;

    @Column(name = "ERROR")
    private boolean error;

    @Column(name = "JOB_REFERENCE")
    private String jobReference;

    @Column(name = "credit")
    private String credit;

    @Column(name = "creator")
    private String creator;
    
    @Column(name = "DELETED")
    private Boolean deleted=Boolean.FALSE;

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

	public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public List<Queue> getQueue() {
        return queue;
    }

    public void setQueue(List<Queue> queue) {
        this.queue = queue;
    }

    public String getTransmissionData() {
        return transmissionData;
    }

    public void setTransmissionData(String transmissionData) {
        this.transmissionData = transmissionData;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getHeadline() {
        return headline;
    }
    public void setHeadline(String headline) {
        this.headline = headline;
    }
    public String getCredit() {
        return credit;
    }
    public void setCredit(String credit) {
        this.credit = credit;
    }
    public String getCreator() {
        return creator;
    }
    public void setCreator(String creator) {
        this.creator = creator;
    }

    @PrePersist
    protected void onCreate() {
        this.dateAdded = new Date();
    }

    public String getJobReference() {
        return jobReference;
    }

    public void setJobReference(String jobReference) {
        this.jobReference = jobReference;
    }
    
    public Boolean isDeleted() {
        return deleted;
    }

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
    
}
