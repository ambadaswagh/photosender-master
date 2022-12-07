package com.cwidanage.photoftp.models;
import javax.persistence.*;


@Entity
@Table(name = "keywords")
public class Keywords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "valore")
    private String valore;

    public Integer getid() {
        return id;
    }

    public void setid(Integer id) {
        this.id = id;
    }

    public String geValore() {
        return valore;
    }

    public void setValore(String valore) {
        this.valore = valore;
    }

}

