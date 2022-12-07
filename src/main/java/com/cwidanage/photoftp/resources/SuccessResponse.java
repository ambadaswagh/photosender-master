package com.cwidanage.photoftp.resources;

/**
 * @author Chathura Widanage
 */
public class SuccessResponse {
    private String error;

    public SuccessResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
