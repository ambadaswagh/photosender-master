package com.cwidanage.photoftp.models;

import javax.persistence.*;
import java.util.List;

/**
 * @author Chathura Widanage
 */
@Entity
@Table(name = "ftp_group")
public class FTPGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<FTPAccount> ftp_accounts;

    @Column(name = "DELETED")
    private Boolean deleted = Boolean.FALSE;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<FTPAccount> getFtpAccounts() {
        return ftp_accounts;
    }

    public void setFtpAccounts(List<FTPAccount> ftpAccounts) {
        this.ftp_accounts = ftpAccounts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

	@Override
	public String toString() {
		return "FTPGroup [id=" + id + ", name=" + name + ", ftpAccounts=" + ftp_accounts + ", deleted=" + deleted + "]";
	}
    
    
}
