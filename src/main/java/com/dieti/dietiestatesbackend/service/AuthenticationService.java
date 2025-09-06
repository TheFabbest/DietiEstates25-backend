package com.dieti.dietiestatesbackend.service;

import com.dieti.dietiestatesbackend.dto.request.GoogleAuthRequest;
import com.dieti.dietiestatesbackend.dto.request.SignupRequest;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.security.RefreshTokenProvider;
import com.dieti.dietiestatesbackend.security.RefreshTokenProvider.TokenValidationResult;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

    private final UserQueryService userQueryService;
    private final UserManagementService userManagementService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;
    private final RefreshTokenProvider refreshTokenProvider;

    public AuthenticationService(UserQueryService userQueryService,
                                 UserManagementService userManagementService,
                                 PasswordEncoder passwordEncoder,
                                 PasswordValidator passwordValidator,
                                 RefreshTokenProvider refreshTokenProvider) {
        this.userQueryService = userQueryService;
        this.userManagementService = userManagementService;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidator = passwordValidator;
        this.refreshTokenProvider = refreshTokenProvider;
    }

    public User registerNewUser(SignupRequest signupRequest) {
        if (userQueryService.doesUserExist(signupRequest.getEmail())) {
            throw new IllegalStateException("Utente gia' registrato");
        }

        if (!passwordValidator.isStrong(signupRequest.getPassword())) {
            throw new IllegalArgumentException("Password debole: deve contenere almeno 8 caratteri, di cui almeno una lettera maiuscola, una lettera minuscola, un numero e un carattere speciale.");
        }

        userManagementService.createUser(signupRequest.getEmail(),
                                        signupRequest.getPassword(),
                                        signupRequest.getUsername(),
                                        signupRequest.getName(),
                                        signupRequest.getSurname());

        return userQueryService.getUserByUsername(signupRequest.getUsername());
    }

    public void handleGoogleAuth(String email, GoogleAuthRequest googleAuthRequest) {
        if (!userQueryService.doesUserExist(email)) {
            if (googleAuthRequest.getUsername() != null && !googleAuthRequest.getUsername().isEmpty()) {
                try {
                    userManagementService.createGoogleUser(email, googleAuthRequest.getUsername(),
                                                          googleAuthRequest.getName(), googleAuthRequest.getSurname());
                } catch (IllegalStateException e) {
                    throw new IllegalStateException("Impossibile creare l'utente Google: " + e.getMessage());
                }
            } else {
                throw new IllegalStateException("L'utente non esiste e non è stato fornito uno username per la creazione.");
            }
        }
    }

    @Transactional
    public LogoutResult logout(String refreshToken) {
        try {
            TokenValidationResult validation = refreshTokenProvider.validateTokenForLogout(refreshToken);

            if (!validation.isValid()) {
                return new LogoutResult(false, validation.message(), HttpStatus.BAD_REQUEST);
            }

            refreshTokenProvider.deleteByTokenValue(refreshToken);

            return new LogoutResult(true, "Logout effettuato con successo", HttpStatus.OK);

        } catch (Exception e) {
            return new LogoutResult(false, "Errore interno durante il logout", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public record LogoutResult(boolean success, String message, HttpStatus status) {}
}