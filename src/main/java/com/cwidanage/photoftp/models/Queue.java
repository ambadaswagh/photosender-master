package com.cwidanage.photoftp.models;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.List;

/**
 * @author Chathura Widanage
 */
@Entity
@Table(name = "queue")
public class Queue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    private Photo photo;

    @OneToOne
    @JoinColumn(name = "FTP_ACCOUNT")
    private FTPAccount ftpAccount;

    @Column(name = "PROCESSED")
    private boolean processed;

    @Column(name = "PROCESSED_TIME")
    private Date processedTime;

    @Column(name = "LOG")
    private String log = "";

    @Column(name = "ERROR")
    private boolean error;

    @Column(name = "DATE_ADDED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAdded;

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

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public FTPAccount getFtpAccount() {
        return ftpAccount;
    }

    public void setFtpAccount(FTPAccount ftpAccount) {
        this.ftpAccount = ftpAccount;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public Date getProcessedTime() {
        return processedTime;
    }

    public void setProcessedTime(Date processedTime) {
        this.processedTime = processedTime;
    }

	public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    @PrePersist
    protected void onCreate() {
        this.dateAdded = new Date();
    }
}
