package com.dieti.dietiestatesbackend.security;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);


    private final AccessTokenProvider accessTokenProvider;
    
    public JwtAuthenticationFilter(AccessTokenProvider accessTokenProvider) {
        this.accessTokenProvider = accessTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String token = extractTokenFromRequest(request);
        
        if (token != null) {
            UsernamePasswordAuthenticationToken authentication = authenticateUser(token);
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        // Fallback per header "Bearer" usato in alcuni endpoint
        bearerToken = request.getHeader("Bearer");
        if (bearerToken != null) {
            return bearerToken;
        }
        return null;
    }
    
    private UsernamePasswordAuthenticationToken authenticateUser(String token) {
        try {
            String username = accessTokenProvider.getUsernameFromToken(token);
            
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (accessTokenProvider.validateToken(token)) {
                    Long id = accessTokenProvider.getIdFromToken(token);
                    Boolean isManager = accessTokenProvider.getIsManagerFromToken(token);
                    List<String> roles = accessTokenProvider.getRolesFromToken(token);
                    
                    List<GrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                    
                    AuthenticatedUser principal = new AuthenticatedUser(
                        id, username, isManager != null && isManager, authorities
                    );
                    
                    logger.debug("Authenticated principal class={}, id={}, isManager={}",
                        principal.getClass(), principal.getId(), principal.isManager());
                    
                    return new UsernamePasswordAuthenticationToken(principal, null, authorities);
                }
            }
        } catch (ExpiredJwtException e) {
            logger.warn("Token JWT scaduto: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.warn("Token JWT malformato: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.warn("Token JWT non supportato: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.warn("Token JWT non valido: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Errore durante la validazione del token JWT", e);
        }
        return null;
    }
}