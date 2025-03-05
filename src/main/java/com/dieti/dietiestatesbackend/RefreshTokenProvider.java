package com.dieti.dietiestatesbackend;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
class RefreshTokenProvider {

  @Value("${jwt.secret}")
  private static final String SECRET_KEY = "UjRoSEFoSVNtQzVUcGJaZVRJMmgxaVhlSm81THhHajVob0M4SWFsaUJ6YnNvZzZ1WklSNkxTUnhaUjJ6UEMzVQ==";

  @Value("${jwt.refresh.expiration}")
  private static final Long REFRESH_TOKEN_DURATION_MS = 604800000l; // 7 days

  static String generateRefreshToken(final String username) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_DURATION_MS);

    Map<String, Object> claims = new HashMap<>();

    String refreshToken = Jwts.builder()
        .setClaims(claims)
        .setSubject(username)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
        .compact();

    RefreshTokenRepository.deleteByUserId(username, SECRET_KEY);
    RefreshTokenRepository.save(refreshToken);
    return refreshToken;
  }

  static boolean validateToken(final String token) {
    TokenHelper th = new TokenHelper(SECRET_KEY);
    return th.validateToken(token);
  }

  static String getUsernameFromToken(final String token) {
    TokenHelper helper = new TokenHelper(SECRET_KEY);
    return helper.getUsernameFromToken(token);
  }

    static boolean isTokenOf(final String user, final String oldRefreshToken) {
        return RefreshTokenRepository.getTokenByUserId(user, SECRET_KEY).equals(oldRefreshToken);
    }
}