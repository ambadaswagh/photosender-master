package com.cwidanage.photoftp.models;

import javax.persistence.*;

@Entity
@Table(name = "copyright")
public class Copyright {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "instructions")
    private String instructions;

    @Column(name = "testo_it", length = 355)
    private String testo_it;


    public Integer getid() {
        return id;
    }

    public void setid(Integer id) {
        this.id = id;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getTesto_it() {
        return testo_it;
    }

    public void setTesto_it(String testo_it) {
        this.testo_it = testo_it;
    }

}











