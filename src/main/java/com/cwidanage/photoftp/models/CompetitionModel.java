package com.cwidanage.photoftp.models;

import java.util.List;

public class CompetitionModel {

	
	private Integer id;

	
	private String nome_it;

	
	private String subject;

	
	private String subjectabb;

	
	private String keyword;

	
	private List<Squadre> squadreList;

	private List<String> teams;

	public List<String> getTeams() {
		return teams;
	}

	public void setTeams(List<String> teams) {
		this.teams = teams;
	}

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