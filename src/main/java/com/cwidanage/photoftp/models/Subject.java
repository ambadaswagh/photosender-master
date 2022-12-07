package com.cwidanage.photoftp.models;

import javax.persistence.*;

@Entity
@Table(name = "subject")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aid")
    private Integer aid;

    @Column(name = "codice")
    private String codice;

    @Column(name = "subject")
    private String subject;

    @Column(name = "categoria")
    private String categoria;

    @Column(name = "subcat")
    private String subcat;

    @Column(name = "subcat1")
    private String subcat1;

    @Column(name = "subcat2")
    private String subcat2;

    @Column(name = "afpcat")
    private String afpcat;

    public Integer getAid() {
        return aid;
    }

    public void setAid(Integer aid) {
        this.aid = aid;
    }

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice= codice;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject= subject;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria= categoria;
    }

    public String getSubcat() {
        return subcat;
    }

    public void setSubcat(String subcat) {
        this.subcat= subcat;
    }

    public String getSubcat1() {
        return subcat1;
    }

    public void setSubcat1(String subcat1) {
        this.subcat1= subcat1;
    }

    public String getSubcat2() {
        return subcat2;
    }

    public void setSubcat2(String subcat2) {
        this.subcat2= subcat2;
    }

    public String getAfpcat() {
        return afpcat;
    }

    public void setAfpcat(String afpcat) {
        this.afpcat= afpcat;

    }
}
