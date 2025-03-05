package com.dieti.dietiestatesbackend;

import java.util.Date;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

class TokenHelper {
  private final String secretKey;

  TokenHelper(String secretKey) {
    this.secretKey = secretKey;
  }

  boolean validateToken(String token, String supposedUsername) {
    final String username = getUsernameFromToken(token);
    return (username.equals(supposedUsername) && !isTokenExpired(token));
  }

  // TODO check safety
  boolean validateToken(String token) {
    return !isTokenExpired(token);
  }

  boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  String getUsernameFromToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }

  Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }

  <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }

  private Claims getAllClaimsFromToken(String token) {
    return Jwts.parser()
        .setSigningKey(secretKey)
        .parseClaimsJws(token)
        .getBody();
  }
}