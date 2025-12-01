package com.dieti.dietiestatesbackend.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
import com.dieti.dietiestatesbackend.service.AuthenticationService;
import com.dieti.dietiestatesbackend.service.UserService;
import com.dieti.dietiestatesbackend.service.emails.EmailService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationService authService;
    private final ScheduledExecutorService scheduler;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenProvider refreshTokenProvider;
    private final AccessTokenProvider accessTokenProvider;
    private final GoogleTokenValidator googleTokenValidator;
    private final EmailService emailService;

    @Autowired
    public AuthController(UserService userService,
                          AuthenticationService authService,
                          ScheduledExecutorService scheduler,
                          AuthenticationManager authenticationManager,
                          RefreshTokenProvider refreshTokenProvider,
                          AccessTokenProvider accessTokenProvider,
                          GoogleTokenValidator googleTokenValidator,
                          EmailService emailService) {
        this.userService = userService;
        this.authService = authService;
        this.scheduler = scheduler;
        this.authenticationManager = authenticationManager;
        this.refreshTokenProvider = refreshTokenProvider;
        this.accessTokenProvider = accessTokenProvider;
        this.googleTokenValidator = googleTokenValidator;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid AuthRequest authRequest) {
        String username = userService.getUsernameFromEmail(authRequest.getEmail());
        if (username == null || username.isEmpty()) {
            return buildErrorResponse("Credenziali non valide", HttpStatus.UNAUTHORIZED);
        }

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, authRequest.getPassword())
        );

        User user = userService.getUserByUsername(username);
        return buildAuthResponseEntity(user, username);
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody @Valid SignupRequest signupRequest) {
        User savedUser = authService.registerNewUser(signupRequest);
        emailService.welcomeMessage(savedUser);
        return buildAuthResponseEntity(savedUser, savedUser.getUsername());
    }

    @PostMapping("/google")
    public ResponseEntity<Object> authWithGoogle(@RequestBody @Valid GoogleAuthRequest googleAuthRequest) throws IOException, GeneralSecurityException {
        GoogleIdToken.Payload payload = googleTokenValidator.validateToken(googleAuthRequest.getIdToken());
        String email = payload.getEmail();
        String username = payload.get("preferred_username") != null ? payload.get("preferred_username").toString() : email.split("@")[0];
        String name = payload.get("given_name").toString();
        String surname = payload.get("family_name").toString();
        authService.handleGoogleAuth(email, username, name, surname);
        User user = userService.getUserByUsername(username);
        emailService.welcomeMessage(user);
        return buildAuthResponseEntity(user, username);
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
        AuthenticationService.LogoutResult result = authService.logout(refreshRequest.getRefreshToken());
        
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
                .toList();

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, availableRoles));
    }

    private ResponseEntity<Object> buildErrorResponse(String message, HttpStatusCode status) {
        return new ResponseEntity<>(message, status);
    }

    // getAgentInfo endpoint moved to UserController to respect SRP
}
