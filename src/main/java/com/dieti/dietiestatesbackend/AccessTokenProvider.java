package com.dieti.dietiestatesbackend;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
class AccessTokenProvider {

  @Value("${jwt.secret}")
  private final static String SECRET_KEY = "UjRoSEFoSVNtQzVUcGJaZVRJMmgxaVhlSm81THhHajVob0M4SWFsaUJ6YnNvZzZ1WklSNkxTUnhaUjJ6UEMzVQ==";

  @Value("${jwt.access.expiration}")
  private final static Long ACCESS_TOKEN_DURATION_MS = 900000l; // 15 minutes

  static String generateAccessToken(String username) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_DURATION_MS);

    Map<String, Object> claims = new HashMap<>();

    return Jwts.builder()
        .setClaims(claims)
        .setSubject(username)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
        .compact();
  }

  static boolean validateToken(String token) {
    TokenHelper th = new TokenHelper(SECRET_KEY);
    return th.validateToken(token);
  }
}