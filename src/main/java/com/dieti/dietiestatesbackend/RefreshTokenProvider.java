package com.dieti.dietiestatesbackend;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@SuppressWarnings("java:S1118")
@Component
class RefreshTokenProvider {

    private static final String SECRET_KEY = System.getenv("REFRESH_TOKEN_SECRET_KEY");

    private static final Long REFRESH_TOKEN_DURATION_MS = 604800000l; // 7 days

    static String generateRefreshToken(final String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_DURATION_MS);

        Map<String, Object> claims = new HashMap<>();
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        String refreshToken = Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key)
            .compact();

        RefreshTokenRepository.save(username, refreshToken);
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
        return RefreshTokenRepository.getTokensByUserId(user).contains(oldRefreshToken);
    }
}