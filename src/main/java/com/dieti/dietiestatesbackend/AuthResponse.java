package com.dieti.dietiestatesbackend;

class AuthResponse {
  private final String accessToken;
  private final String refreshToken;

  AuthResponse(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }
}