package com.dieti.dietiestatesbackend.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dieti.dietiestatesbackend.dto.request.SignupRequest;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.security.RefreshTokenProvider;
import com.dieti.dietiestatesbackend.security.RefreshTokenProvider.TokenValidationResult;

@Service
public class AuthenticationService {

    private final UserQueryService userQueryService;
    private final UserManagementService userManagementService;
    private final PasswordValidator passwordValidator;
    private final RefreshTokenProvider refreshTokenProvider;

    public AuthenticationService(UserQueryService userQueryService,
                                 UserManagementService userManagementService,
                                 PasswordValidator passwordValidator,
                                 RefreshTokenProvider refreshTokenProvider) {
        this.userQueryService = userQueryService;
        this.userManagementService = userManagementService;
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

    public void handleGoogleAuth(String email, String username, String name, String surname) {
        if (!userQueryService.doesUserExist(email)) {
            if (username != null && !username.isEmpty()) {
                try {
                    userManagementService.createGoogleUser(email, username, name, surname);
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