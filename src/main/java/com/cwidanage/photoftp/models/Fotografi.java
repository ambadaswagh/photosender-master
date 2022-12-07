package com.cwidanage.photoftp.models;

import javax.persistence.*;

@Entity
@Table(name = "fotografi")
public class Fotografi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nome_fotografo")
    private String nome_fotografo;

    @Column(name = "iniziali")
    private String iniziali;

    @Column(name = "AuthorsPosition")
    String authorsPosition;

    @Column(name = "email")
    String email;

    @Column(name = "username")
    String username;

    @Column(name = "password")
    String password;

    public Integer getid() {
        return id;
    }

    public void setid(Integer id) {
        this.id = id;
    }


    public String getNome_fotografo() {
        return nome_fotografo;
    }

    public void setNome_fotografo(String nome_fotografo) {
        this.nome_fotografo = nome_fotografo;
    }

    public String getIniziali() {
        return iniziali;
    }

    public void setIniziali(String iniziali) {
        this.iniziali = iniziali;
    }

    public String getAuthorsPosition() {
        return authorsPosition;
    }
    public void setAuthorsPosition(String authorsPosition) {
        this.authorsPosition = authorsPosition;
    }

    public String getemail() {
        return email;
    }
    public void setemail(String email) {
        this.email = email;
    }
    public String getusername() {
        return username;
    }
    public void setusername(String username) {
        this.username = username;
    }
    public String getpassword() {
        return password;
    }
    public void setepassword(String password) {
        this.password = password;
    }


}
