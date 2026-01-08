package com.inmobi.dtos.res;

public class LoginResponse {
    private String accesstToken;
    private String refreshToken;
    private Boolean authenticated;

    public LoginResponse() {
    }

    public LoginResponse(String accesstToken, String refreshToken, Boolean authenticated) {
        this.accesstToken = accesstToken;
        this.refreshToken = refreshToken;
        this.authenticated = authenticated;
    }

    public String getAccesstToken() {
        return accesstToken;
    }

    public void setAccesstToken(String accesstToken) {
        this.accesstToken = accesstToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Boolean getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
    }

}
