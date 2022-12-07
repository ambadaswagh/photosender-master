package com.cwidanage.photoftp.models;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "agenzia")
public class Agenzia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aid")
    private Integer aid;


    @Column(name = "nome_agenzia")
    private String nome_agenzia;

    @Column(name = "indirizzo")
    private String indirizzo;

    @Column(name = "provincia")
    private String provincia;

    @Column(name = "citta")
    private String citta;

    @Column(name = "cap")
    private String cap;

    @Column(name = "nazione")
    private String nazione;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "email")
    @Pattern(regexp="[A-Za-z0-9._%-+]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")
    private String email;

    @Column(name = "sito")
    private String sito;

    public Integer getAid() {
        return aid;
    }

    public void setAid(Integer aid) {
        this.aid = aid;
    }

    public String getNome_agenzia() {
        return nome_agenzia;
    }

    public void setNome_agenzia(String nome_agenzia) {
        this.nome_agenzia = nome_agenzia;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public String getNazione() {
        return nazione;
    }

    public void setNazione(String nazione) {
        this.nazione = nazione;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSito() {
        return sito;
    }

    public void setSito(String sito) {
        this.sito = sito;
    }
}
