package com.cwidanage.photoftp.models;

import javax.persistence.*;

@Entity
@Table(name = "squadre")
public class Squadre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;


    @Column(name = "nome_it")
    private String nomeit;

    @Column(name = "home_venue")
    private String home_venue;

    @Column(name = "abb_nome")
    private String abb_nome;

    @Column(name = "subj")
    private String subj;

    @Column(name = "venue_city")
    private String venue_city;



    public Integer getid() {
        return id;
    }

    public void setid(Integer id) {
        this.id = id;
    }

    public String getNome_it() {
        return nomeit;
    }

    public void setNome_it(String nome_it) {
        this.nomeit= nome_it;
    }

    public String getabb_nome() {
        return abb_nome;
    }
    public void setabb_nome(String abb_nome) {
        this.abb_nome= abb_nome;
    }

    public String getsubj() {
        return subj;
    }

    public void setsubj(String subj) {
        this.subj= subj;
    }

    public void setHome_venue(String home_venue) {
        this.home_venue= home_venue;
    }

    public String getHome_venue() {
        return home_venue;
    }
    
}
