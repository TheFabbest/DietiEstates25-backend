package com.dieti.dietiestatesbackend.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import com.dieti.dietiestatesbackend.entities.RefreshToken;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.repositories.RefreshTokenRepository;
import com.dieti.dietiestatesbackend.repositories.UserRepository;

/**
 * Provider che emette refresh token di tipo opaque (UUID) e salva il loro hash in DB.
 * Implementa rotation single-use: durante la refresh operation il token vecchio viene rimosso
 * e ne viene emesso uno nuovo.
 */
@Component
public class RefreshTokenProvider {

    private static final Long REFRESH_TOKEN_DURATION_MS = 604800000L; // 7 days

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenProvider(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    private String sha256Hex(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Errore hashing token", e);
        }
    }

    // Generate opaque token (raw) and persist its hash
    public String generateRefreshTokenForUser(final User user) {
        String raw = UUID.randomUUID().toString();
        String hash = sha256Hex(raw);
    
        RefreshToken rt = new RefreshToken();
        rt.setTokenValue(hash); // store hash only
        rt.setUser(user);
        // Ensure createdAt is set to satisfy NOT NULL constraint inherited from BaseEntity
        rt.setCreatedAt(LocalDateTime.now());
        rt.setExpiryDate(LocalDateTime.now().plusSeconds(REFRESH_TOKEN_DURATION_MS / 1000));
    
        refreshTokenRepository.save(rt);
        return raw;
    }

    // Legacy convenience: accept username
    public String generateRefreshToken(final String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalStateException("Utente non trovato per username: " + username));
        return generateRefreshTokenForUser(user);
    }

    // Validate opaque raw token by hashing and checking DB entry + expiry
    public boolean validateToken(final String rawToken) {
        if (rawToken == null || rawToken.isEmpty()) return false;
        String hash = sha256Hex(rawToken);
        Optional<RefreshToken> opt = refreshTokenRepository.findByTokenValue(hash);
        return opt.filter(rt -> rt.getExpiryDate() != null && rt.getExpiryDate().isAfter(LocalDateTime.now())).isPresent();
    }

    // Get username from raw token
    public String getUsernameFromToken(final String rawToken) {
        if (rawToken == null || rawToken.isEmpty()) return null;
        String hash = sha256Hex(rawToken);
        return refreshTokenRepository.findByTokenValue(hash)
            .map(rt -> rt.getUser() != null ? rt.getUser().getUsername() : null)
            .orElse(null);
    }

    // Check token belongs to username
    public boolean isTokenOf(final String username, final String rawToken) {
        if (rawToken == null || rawToken.isEmpty()) return false;
        String hash = sha256Hex(rawToken);
        return refreshTokenRepository.findByTokenValue(hash)
            .map(rt -> rt.getUser() != null && username.equals(rt.getUser().getUsername()))
            .orElse(false);
    }

    // Rotate: validate old token, remove it, emit new one for same user
    public String rotateRefreshToken(final String oldRawToken) {
        if (oldRawToken == null || oldRawToken.isEmpty()) {
            throw new IllegalArgumentException("Old refresh token is required");
        }
        String oldHash = sha256Hex(oldRawToken);
        Optional<RefreshToken> opt = refreshTokenRepository.findByTokenValue(oldHash);
        if (opt.isEmpty()) {
            throw new IllegalStateException("Refresh token non trovato o già invalidato");
        }
        RefreshToken existing = opt.get();
        User user = existing.getUser();
        // delete old
        refreshTokenRepository.deleteByTokenValue(oldHash);
        // create new
        return generateRefreshTokenForUser(user);
    }

    // Delete by raw token (hashes internally)
    @Transactional
    public void deleteByTokenValue(final String rawToken) {
        if (rawToken == null || rawToken.isEmpty()) return;
        String hash = sha256Hex(rawToken);
        refreshTokenRepository.deleteByTokenValue(hash);
    }

    // Validate token for logout and return username on success
    public TokenValidationResult validateTokenForLogout(String rawToken) {
        if (rawToken == null || rawToken.isEmpty()) {
            return new TokenValidationResult(false, "Il refreshToken è obbligatorio", null);
        }
        if (!validateToken(rawToken)) {
            return new TokenValidationResult(false, "Il refreshToken non è valido o è scaduto", null);
        }
        String username = getUsernameFromToken(rawToken);
        if (username == null || !isTokenOf(username, rawToken)) {
            return new TokenValidationResult(false, "Il refreshToken non corrisponde all'utente", null);
        }
        return new TokenValidationResult(true, "Token valido", username);
    }

    /* ---------- helper record ---------- */
    public record TokenValidationResult(boolean isValid, String message, String username) {}
}