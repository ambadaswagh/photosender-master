package com.cwidanage.photoftp.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "competition")
public class Competition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nome_it")
    private String nome_it;

    @Column(name = "subject")
    private String subject;

    @Column(name = "subjectabb")
    private String subjectabb;

    @Column(name = "keyword")
    private String keyword;

    @ManyToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinTable(
            name = "comp_squad_link")
    private  List<Squadre> squadreList=new ArrayList<>();;
    

	public List<Squadre> getSquadreList() {
		return squadreList;
	}

	public void setSquadreList(List<Squadre> squadreList) {
		this.squadreList = squadreList;
	}

	public Integer getid() {
        return id;
    }

    public void setCid(Integer id) {
        this.id = id;
    }

    public String getNome_it() {
        return nome_it;
    }

    public void setNome_it(String nome_it) {
        this.nome_it = nome_it;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubjectabb() {
        return subjectabb;
    }

    public void setSubjectabb(String subjectabb) {
        this.subjectabb = subjectabb;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeywords(String keyword) {
        this.keyword = keyword;
    }

}