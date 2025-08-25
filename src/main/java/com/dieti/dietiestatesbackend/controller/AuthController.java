package com.dieti.dietiestatesbackend.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dieti.dietiestatesbackend.dto.request.AuthRequest;
import com.dieti.dietiestatesbackend.dto.request.GoogleAuthRequest;
import com.dieti.dietiestatesbackend.dto.request.RefreshRequest;
import com.dieti.dietiestatesbackend.dto.request.SignupRequest;
import com.dieti.dietiestatesbackend.dto.response.AuthResponse;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.security.AccessTokenProvider;
import com.dieti.dietiestatesbackend.security.GoogleTokenValidator;
import com.dieti.dietiestatesbackend.security.RefreshTokenProvider;
import com.dieti.dietiestatesbackend.service.AuthService;
import com.dieti.dietiestatesbackend.service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import jakarta.validation.Valid;

@RestController
@Validated
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;
    private final AuthService authService;
    private final ScheduledExecutorService scheduler;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenProvider refreshTokenProvider;
    private final AccessTokenProvider accessTokenProvider;
    private final GoogleTokenValidator googleTokenValidator;
    
    @Autowired
    public AuthController(UserService userService,
                          AuthService authService,
                          ScheduledExecutorService scheduler,
                          AuthenticationManager authenticationManager,
                          RefreshTokenProvider refreshTokenProvider,
                          AccessTokenProvider accessTokenProvider,
                          GoogleTokenValidator googleTokenValidator) {
        this.userService = userService;
        this.authService = authService;
        this.scheduler = scheduler;
        this.authenticationManager = authenticationManager;
        this.refreshTokenProvider = refreshTokenProvider;
        this.accessTokenProvider = accessTokenProvider;
        this.googleTokenValidator = googleTokenValidator;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid AuthRequest authRequest) {
        try {
            // Prima ricava lo username associato all'email, altrimenti Spring cercherà l'email come username
            String username = userService.getUsernameFromEmail(authRequest.getEmail());
            if (username == null || username.isEmpty()) {
                return buildErrorResponse("Credenziali non valide", HttpStatus.UNAUTHORIZED);
            }

            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, authRequest.getPassword())
            );

            User user = userService.getUserByUsername(username);
            return buildAuthResponseEntity(user, username);
        } catch (BadCredentialsException ex) {
            logger.warn("Tentativo di login fallito per {}: {}", authRequest.getEmail(), ex.getMessage());
            return buildErrorResponse("Credenziali non valide", HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            logger.error("Errore durante l'autenticazione: {}", ex.getMessage());
            return buildErrorResponse("Errore durante l'autenticazione", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody @Valid SignupRequest signupRequest) {
        try {
            // Create user and receive the persisted entity directly
            User savedUser = authService.registerNewUser(signupRequest);
            return buildAuthResponseEntity(savedUser, savedUser.getUsername());
        } catch (IllegalStateException e) {
            logger.warn("Registrazione non valida: {}", e.getMessage());
            return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
        } catch (IllegalArgumentException e) {
            logger.warn("Dati di registrazione non validi: {}", e.getMessage());
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Errore durante la registrazione! {}", e.getMessage());
            return buildErrorResponse("Errore durante la registrazione.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/authwithgoogle")
    public ResponseEntity<Object> authWithGoogle(@RequestBody @Valid GoogleAuthRequest googleAuthRequest) {
        try {
            GoogleIdToken.Payload payload = googleTokenValidator.validateToken(googleAuthRequest.getToken());
            String email = payload.getEmail();

            authService.handleGoogleAuth(email, googleAuthRequest);

            User user = userService.getUserByUsername(googleAuthRequest.getUsername());
            return buildAuthResponseEntity(user, googleAuthRequest.getUsername());

        } catch (IOException | GeneralSecurityException e) {
            logger.warn("Token Google non valido: {}", e.getMessage());
            return buildErrorResponse("Token Google non valido", HttpStatusCode.valueOf(498));
        } catch (IllegalArgumentException e) {
            logger.warn("Autenticazione Google fallita: {}", e.getMessage());
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Errore durante l'autenticazione con Google: {}", e.getMessage());
            return buildErrorResponse("Errore durante l'autenticazione con Google.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refreshAccessToken(@RequestBody @Valid RefreshRequest refreshRequest) {
        String oldRefreshToken = refreshRequest.getRefreshToken();
        String username = refreshTokenProvider.getUsernameFromToken(oldRefreshToken);
        
        if (refreshTokenProvider.validateToken(oldRefreshToken) && refreshTokenProvider.isTokenOf(username, oldRefreshToken)) {
            User user = userService.getUserByUsername(username);
            scheduler.schedule(() -> refreshTokenProvider.deleteByTokenValue(oldRefreshToken), 10, TimeUnit.SECONDS);
            return buildAuthResponseEntity(user, username);
        }
        return buildErrorResponse("Refresh token non valido o scaduto", HttpStatusCode.valueOf(498));
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(@RequestBody @Valid RefreshRequest refreshRequest) {
        AuthService.LogoutResult result = authService.logout(refreshRequest.getRefreshToken());
        
        if (result.success()) {
            return ResponseEntity.ok().build();
        } else {
            return new ResponseEntity<>(result.message(), result.status());
        }
    }

    private ResponseEntity<Object> buildAuthResponseEntity(User user, String username) {
        String accessToken = accessTokenProvider.generateAccessToken(user);
        String refreshToken = refreshTokenProvider.generateRefreshToken(username);

        List<String> availableRoles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, availableRoles));
    }

    private ResponseEntity<Object> buildErrorResponse(String message, HttpStatusCode status) {
        return new ResponseEntity<>(message, status);
    }

    // getAgentInfo endpoint moved to UserController to respect SRP
}
