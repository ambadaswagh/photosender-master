package com.cwidanage.photoftp.models;

import javax.persistence.*;

@Entity
@Table(name = "venues")
public class Venues {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "venue_name")
    private String venuename;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "nation")
    private String nation;

    @Column(name = "iso")
    private String iso;


    public Integer getid() {
        return id;
    }

    public void setid(Integer id) {
        this.id = id;
    }

    public String getVenue_name() {
        return venuename;
    }

    public void setVenue_name(String venue_name) {
        this.venuename = venue_name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    
    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }


}
