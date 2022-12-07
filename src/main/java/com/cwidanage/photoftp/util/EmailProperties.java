package com.cwidanage.photoftp.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by Asim on 1/18/2018.
 */
@Component
@ConfigurationProperties(prefix = "photoftp.mail")
public class EmailProperties {

    private String host;
    private String port;
    private String username;
    private String password;
    private boolean auth;
    private String socketFactoryPort;
    private String socketFactoryClass;

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSocketFactoryPort() {
        return socketFactoryPort;
    }

    public String getSocketFactoryClass() {
        return socketFactoryClass;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public void setSocketFactoryPort(String socketFactoryPort) {
        this.socketFactoryPort = socketFactoryPort;
    }

    public void setSocketFactoryClass(String socketFactoryClass) {
        this.socketFactoryClass = socketFactoryClass;
    }
}
