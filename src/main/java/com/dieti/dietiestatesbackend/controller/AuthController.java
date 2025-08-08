package com.dieti.dietiestatesbackend.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.dieti.dietiestatesbackend.dto.response.AuthResponse;
import com.dieti.dietiestatesbackend.dto.response.UserResponse;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.security.AccessTokenProvider;
import com.dieti.dietiestatesbackend.security.GoogleTokenValidator;
import com.dieti.dietiestatesbackend.security.RefreshTokenProvider;
import com.dieti.dietiestatesbackend.security.RefreshTokenRepository;
import com.dieti.dietiestatesbackend.service.UserService;
import com.dieti.dietiestatesbackend.util.DaemonThreadFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

@RestController
public class AuthController {
    private static final Logger logger = Logger.getLogger(AuthController.class.getName());
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new DaemonThreadFactory());
    
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        if (userService.doesUserExist(email, password)) {
            String username = userService.getUsernameFromEmail(email);
            String accessToken = AccessTokenProvider.generateAccessToken(username);
            String refreshToken = RefreshTokenProvider.generateRefreshToken(username);
            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
        } else {
            return new ResponseEntity<>("Credenziali non valide", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String username = body.get("username");
        // TODO verify email
        if (userService.doesUserExist(email)) {
            return new ResponseEntity<>("Utente gia' registrato", HttpStatus.CONFLICT);
        }
        else {
            String password = body.get("password");
            String name = body.get("name");
            String surname = body.get("surname");
            if (!userService.isPasswordStrong(password)){
                return new ResponseEntity<>("Password debole: deve contenere almeno 8 caratteri, di cui almeno una lettera maiuscola, una lettera minuscola, un numero e un carattere speciale (@ # $ % ^ & + =).", HttpStatus.BAD_REQUEST);
            }

            try {
                userService.createUser(email, password, username, name, surname);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Errore! {0}", e.getMessage());
                return new ResponseEntity<>(userService.getErrorMessageUserCreation(e), HttpStatus.BAD_REQUEST);
            }
        
            String accessToken = AccessTokenProvider.generateAccessToken(username);
            String refreshToken = RefreshTokenProvider.generateRefreshToken(username);
            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
        }
    }

    @PostMapping("/authwithgoogle")
    public ResponseEntity<Object> authWithGoogle(@RequestBody Map<String, String> body) {
        try {
            GoogleIdToken.Payload payload = GoogleTokenValidator.validateToken(body.get("token"));
            String email = payload.getEmail();
            String username = body.get("username");
            if (!userService.doesUserExist(email)) {
                if (body.containsKey("username")) {
                    String name = body.get("name");
                    String surname = body.get("surname");
                    try {
                        userService.createUser(email, "", username, name, surname);
                    }
                    catch (SQLException e){
                        return new ResponseEntity<>(userService.getErrorMessageUserCreation(e), HttpStatus.BAD_REQUEST);
                    }
                }
                else {
                    return new ResponseEntity<>("L'utente non esiste", HttpStatus.NOT_FOUND);
                }
            }
            String accessToken = AccessTokenProvider.generateAccessToken(username);
            String refreshToken = RefreshTokenProvider.generateRefreshToken(username);
            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
        } catch (IOException | GeneralSecurityException e) {
            return new ResponseEntity<>("Token google non valido", HttpStatusCode.valueOf(498));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refreshAccessToken(@RequestBody Map<String, String> body) {
        String oldRefreshToken = body.get("refreshToken");
        String username = RefreshTokenProvider.getUsernameFromToken(oldRefreshToken);
        if (RefreshTokenProvider.validateToken(oldRefreshToken) && RefreshTokenProvider.isTokenOf(username, oldRefreshToken)) {
            String accessToken = AccessTokenProvider.generateAccessToken(username);
            String refreshToken = RefreshTokenProvider.generateRefreshToken(username);
            scheduler.schedule(()->RefreshTokenRepository.deleteUserToken(username, oldRefreshToken), 10, TimeUnit.SECONDS);
            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
        }
        return new ResponseEntity<>("Refresh token non valido o scaduto", HttpStatusCode.valueOf(498));
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(@RequestBody Map<String, String> body) {
        String oldRefreshToken = body.get("refreshToken");
        RefreshTokenRepository.deleteUserToken(oldRefreshToken);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/agent/info/{id}")
    public ResponseEntity<Object> getAgentInfo(
            @PathVariable("id") Long id,
            @RequestHeader(value = "Bearer", required = true) String accessToken) {
        if (accessToken == null || !AccessTokenProvider.validateToken(accessToken)) {
            return new ResponseEntity<>("Token non valido o scaduto", HttpStatusCode.valueOf(498));
        }
        User user = userService.getUserFromID(id);
        if (user != null && user.isAgent()) {
            UserResponse response = new UserResponse();
            response.setEmail(user.getEmail());
            response.setFullName(user.getFirstName()+ " " + user.getLastName());
            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>("Agente non trovato", HttpStatus.NOT_FOUND);
        }
    }
}