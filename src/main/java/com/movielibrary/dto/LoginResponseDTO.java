package com.movielibrary.dto;

public class LoginResponseDTO {

    private final String token;
    private final String tokenType = "Bearer";

    public LoginResponseDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }
}
