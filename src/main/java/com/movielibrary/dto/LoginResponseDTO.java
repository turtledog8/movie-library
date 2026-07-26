package com.movielibrary.dto;

/**
 * Response returned after a successful login, carrying the issued JWT
 */
public class LoginResponseDTO {

    private final String token;
    private final String tokenType = "Bearer";

    /**
     * @param token the JWT issued for the authenticated user
     */
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
