package com.dieti.dietiestatesbackend.service;

import com.dieti.dietiestatesbackend.dto.request.GoogleAuthRequest;
import com.dieti.dietiestatesbackend.dto.request.SignupRequest;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.repositories.UserRepository;
import com.dieti.dietiestatesbackend.security.RefreshTokenProvider;
import com.dieti.dietiestatesbackend.security.RefreshTokenProvider.TokenValidationResult;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserService userService;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final PasswordValidator passwordValidator;

    private final RefreshTokenProvider refreshTokenProvider;

    public AuthService(UserService userService,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       PasswordValidator passwordValidator,
                       RefreshTokenProvider refreshTokenProvider) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidator = passwordValidator;
        this.refreshTokenProvider = refreshTokenProvider;
    }

    public User registerNewUser(SignupRequest signupRequest) {
        if (userService.doesUserExist(signupRequest.getEmail())) {
            throw new IllegalStateException("Utente gia' registrato");
        }
 
        if (!passwordValidator.isStrong(signupRequest.getPassword())) {
            throw new IllegalArgumentException("Password debole: deve contenere almeno 8 caratteri, di cui almeno una lettera maiuscola, una lettera minuscola, un numero e un carattere speciale.");
        }

        User user = new User();
        user.setCreatedAt(LocalDateTime.now());
        user.setEmail(signupRequest.getEmail());
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setFirstName(signupRequest.getName());
        user.setLastName(signupRequest.getSurname());
        // I campi isAgent e isManager sono già inizializzati a false nel costruttore di User
        User saved = userRepository.save(user);
        return saved;
    }

    public void handleGoogleAuth(String email, GoogleAuthRequest googleAuthRequest) {
        if (!userService.doesUserExist(email)) {
            if (googleAuthRequest.getUsername() != null && !googleAuthRequest.getUsername().isEmpty()) {
                try {
                    userService.createGoogleUser(email, googleAuthRequest.getUsername(),
                                               googleAuthRequest.getName(), googleAuthRequest.getSurname());
                } catch (IllegalStateException e) {
                    // Rilancia l'eccezione con un messaggio più appropriato per il contesto di autenticazione Google
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

            String username = validation.username();
            // rimuove il token persistente
            refreshTokenProvider.deleteByTokenValue(refreshToken);

            return new LogoutResult(true, "Logout effettuato con successo", HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Errore durante il logout: {}", e.getMessage());
            return new LogoutResult(false, "Errore interno durante il logout", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public record LogoutResult(boolean success, String message, HttpStatus status) {}
}