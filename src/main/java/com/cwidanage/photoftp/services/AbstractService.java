package com.cwidanage.photoftp.services;

/**
 * @author Chathura Widanage
 */
public class AbstractService {
    public boolean isEmptyString(String string) {
        return string == null || string.trim().isEmpty();
    }
}
