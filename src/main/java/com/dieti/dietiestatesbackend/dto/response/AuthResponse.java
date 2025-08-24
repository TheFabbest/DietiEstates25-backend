package com.dieti.dietiestatesbackend.dto.response;

import java.util.List;

public class AuthResponse {
    private final String accessToken;
    private final String refreshToken;
    private final List<String> availableRoles;

    //! Insensatezza qui!
    public AuthResponse(String accessToken, String refreshToken) {
        this(accessToken, refreshToken, null);
    }

    public AuthResponse(String accessToken, String refreshToken, List<String> availableRoles) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.availableRoles = availableRoles;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public List<String> getAvailableRoles() {
        return availableRoles;
    }
}