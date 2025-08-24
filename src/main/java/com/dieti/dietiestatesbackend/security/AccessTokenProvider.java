package com.dieti.dietiestatesbackend.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import com.dieti.dietiestatesbackend.entities.User;

import jakarta.annotation.PostConstruct;

/**
 * Componente per generazione/estrazione dati token di accesso.
 * Non usa metodi statici: è iniettato dove necessario.
 */
@Component
public class AccessTokenProvider {

  private SecretKey signingKey;
  private final TokenHelper tokenHelper;
 
  private final String secretKey;
 
  @Value("${access.token.duration-ms:900000}")
  private Long accessTokenDurationMs;
 
  public AccessTokenProvider(TokenHelper tokenHelper, @Value("${jwt.secret.access-token}") String secretKey) {
    this.tokenHelper = tokenHelper;
    this.secretKey = secretKey;
  }
 
  @PostConstruct
  public void init() {
    if (secretKey == null || secretKey.isBlank() || secretKey.length() < 32) {
      throw new IllegalStateException("JWT access token secret is missing or too short (min 32 chars). Set 'jwt.secret.access-token' property or JWT_SECRET env var.");
    }
    this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
  }
 
  public String generateAccessToken(User user) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + (accessTokenDurationMs != null ? accessTokenDurationMs : 900000L));
 
    SecretKey keyToUse = signingKey;
 
    List<String> roles = user != null ?
        user.getAuthorities().stream()
            .map(authority -> authority.getAuthority())
            .collect(Collectors.toList()) :
        List.of("ROLE_USER");
 
    return Jwts.builder()
        .subject(user != null ? user.getUsername() : "")
        .claim(JwtClaims.CLAIM_ID, user != null ? user.getId() : null)
        .claim(JwtClaims.CLAIM_IS_MANAGER, user != null ? user.isManager() : false)
        .claim(JwtClaims.CLAIM_ROLES, roles)
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(keyToUse)
        .compact();
  }

  public boolean validateToken(String token) {
    return tokenHelper != null && tokenHelper.validateToken(token);
  }

  public Long getIdFromToken(String token) {
    try {
      JwtClaims jc = tokenHelper != null ? tokenHelper.getClaimsFromToken(token) : null;
      return jc != null ? jc.getId() : null;
    } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
      return null;
    }
  }

  public Boolean getIsManagerFromToken(String token) {
    try {
      JwtClaims jc = tokenHelper != null ? tokenHelper.getClaimsFromToken(token) : null;
      return jc != null && jc.getIsManager() != null ? jc.getIsManager() : false;
    } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
      return null;
    }
  }

  public List<String> getRolesFromToken(String token) {
    try {
      JwtClaims jc = tokenHelper != null ? tokenHelper.getClaimsFromToken(token) : null;
      return jc != null && jc.getRoles() != null ? jc.getRoles() : List.of();
    } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
      return List.of();
    }
  }

  public String getUsernameFromToken(String token) {
    try {
      return tokenHelper != null ? tokenHelper.getUsernameFromToken(token) : null;
    } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
      return null;
    }
  }
}