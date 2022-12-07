package com.cwidanage.photoftp.models;

import javax.persistence.*;

/**
 * @author Chathura Widanage
 */
@Entity
@Table(name = "ftp_account")
public class FTPAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name="NAME",nullable = false)
    private String name;

    @Column(name = "HOST", nullable = false)
    private String host;

    @Column(name = "USERNAME",nullable = false)
    private String username;

    @Column(name="PASSWORD",nullable = false)
    private String password;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "DIRECTORY")
    private String directory;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "DELETED")
    private Boolean deleted=Boolean.FALSE;

    @Column(name = "CREATE_FOLDER")
    private Boolean createFolder=Boolean.FALSE;

    @Column(name = "SFTP_ENABLED_FLAG", nullable = false)
    private Boolean sftpEnabled = Boolean.FALSE;


    public Boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public Boolean getDeleted() {
        return deleted;
    }

    public Boolean getCreateFolder() {
        return createFolder;
    }

    public void setCreateFolder(Boolean createFolder) {
        this.createFolder = createFolder;
    }

    public Boolean getSftpEnabled() {
        return sftpEnabled;
    }

    public void setSftpEnabled(Boolean sftpEnabled) {
        this.sftpEnabled = sftpEnabled;
    }

    @Override
    public String toString() {
        return "FTPAccount [id=" + id + ", name=" + name + ", host=" + host + ", username=" + username + ", password="
                + password + ", description=" + description + ", directory=" + directory + ", email=" + email +", deleted=" + deleted
                + ", createFolder=" + createFolder +", Sftp Enabled="+sftpEnabled+"]";
    }


}