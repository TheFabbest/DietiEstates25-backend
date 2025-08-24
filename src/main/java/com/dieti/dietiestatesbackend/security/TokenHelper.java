package com.dieti.dietiestatesbackend.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * TokenHelper è ora un componente Spring responsabile della lettura/validazione
 * dei token JWT e della deserializzazione dei claim. Centralizza l'uso di
 * ObjectMapper e la gestione della secret key.
 */
@Component
public class TokenHelper {
    private final ObjectMapper mapper;
    private final String secretKeyValue;
    private SecretKey key;
 
    public TokenHelper(ObjectMapper mapper, @Value("${jwt.secret.access-token}") String secretKeyValue) {
        this.mapper = mapper;
        this.secretKeyValue = secretKeyValue;
    }
 
    @PostConstruct
    private void init() {
        if (secretKeyValue == null || secretKeyValue.isBlank() || secretKeyValue.length() < 32) {
            throw new IllegalStateException("JWT access token secret is missing or too short (min 32 chars). Set 'jwt.secret.access-token' property.");
        }
        this.key = Keys.hmacShaKeyFor(secretKeyValue.getBytes(StandardCharsets.UTF_8));
    }

    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration == null || expiration.before(new Date());
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Metodo generico per estrarre un singolo claim tramite una funzione.
     * Ritorna null in caso di token non valido o eccezioni di parsing.
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = getAllClaimsFromToken(token);
            return claimsResolver.apply(claims);
        } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Restituisce l'intero payload mappato su JwtClaims. Centralizza la conversione
     * usando l'ObjectMapper iniettato (singleton Spring).
     */
    public JwtClaims getClaimsFromToken(String token) {
        return getClaimFromToken(token, claims -> mapper.convertValue(claims, JwtClaims.class));
    }

    private Claims getAllClaimsFromToken(String token) throws ExpiredJwtException,
                                        UnsupportedJwtException,
                                        MalformedJwtException,
                                        IllegalArgumentException {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}