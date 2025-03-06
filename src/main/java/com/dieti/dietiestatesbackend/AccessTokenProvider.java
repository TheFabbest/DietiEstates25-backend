package com.dieti.dietiestatesbackend;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
class AccessTokenProvider {

  @Value("${jwt.secret}")
  private final static String SECRET_KEY = "UjRoSEFoSVNtQzVUcGJaZVRJMmgxaVhlSm81THhHajVob0M4SWFsaUJ6YnNvZzZ1WklSNkxTUnhaUjJ6UEMzVQ==";

  @Value("${jwt.access.expiration}")
  private final static Long ACCESS_TOKEN_DURATION_MS = 900000l; // 15 minutes

  static String generateAccessToken(String username) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_DURATION_MS);

    SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(key)
        .compact();
  }

  static boolean validateToken(String token) {
    TokenHelper th = new TokenHelper(SECRET_KEY);
    return th.validateToken(token);
  }
}