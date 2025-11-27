package com.dieti.dietiestatesbackend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dieti.dietiestatesbackend.dto.request.ChangePasswordRequest;
import com.dieti.dietiestatesbackend.dto.request.CreateUserRequest;
import com.dieti.dietiestatesbackend.dto.request.SignupRequest;
import com.dieti.dietiestatesbackend.dto.response.UserResponse;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.security.AuthenticatedUser;
import com.dieti.dietiestatesbackend.service.UserService;
import com.dieti.dietiestatesbackend.service.emails.EmailService;
import com.dieti.dietiestatesbackend.util.RandomPasswordGenerator;

import jakarta.validation.Valid;

@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService, EmailService emailService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/info/me")
    public ResponseEntity<UserResponse> getMyInfo(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        User user = userService.getUser(authenticatedUser.getId());
        UserResponse response = new UserResponse();
        response.setEmail(user.getEmail());
        response.setFullName(user.getFirstName() + " " + user.getLastName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/agent/info/{id}")
    @PreAuthorize("@securityUtil.isAgentOrManager(#authentication.principal, #id)")
    public ResponseEntity<Object> getAgentInfo(@PathVariable("id") Long id) {
        User user = userService.getUser(id);
        if (user != null && user.isAgent()) {
            UserResponse response = new UserResponse();
            response.setEmail(user.getEmail());
            response.setFullName(user.getFirstName() + " " + user.getLastName());
            return ResponseEntity.ok(response);
        } else {
            if (user == null) {
                return new ResponseEntity<>("Utente non trovato", HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>("L'utente non è un agente", HttpStatus.FORBIDDEN);
            }
        }
    }

    @PostMapping("/agent/create")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Object> createAgent(@RequestBody @Valid CreateUserRequest toBeCreated, @AuthenticationPrincipal AuthenticatedUser manager) {
        if (userService.doesUserExist(toBeCreated.getEmail())) {
            try {
                userService.addAgentRole(toBeCreated.getUsername(), userService.getUser(manager.getId()));
                return new ResponseEntity<>("Agent role added to existing user", HttpStatus.OK);
            } catch (Exception e) {
                logger.error("Errore durante l'aggiunta del ruolo agente", e);
                return new ResponseEntity<>("Errore durante l'aggiunta del ruolo agente: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            try {
                String generatedPassword = RandomPasswordGenerator.generateRandom();
                SignupRequest signupRequest = new SignupRequest(toBeCreated, generatedPassword);
                userService.createAgent(signupRequest, userService.getUser(manager.getId()));
                emailService.sendAgentAccountCreatedEmail(signupRequest.getEmail(), signupRequest.getPassword());
                return new ResponseEntity<>("Agent created", HttpStatus.CREATED);
            } catch (Exception e) {
                logger.error("Errore durante la creazione dell'agente", e);
                return new ResponseEntity<>("Errore durante la creazione dell'agente: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

    }

    @PostMapping("/manager/create")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Object> createManager(@RequestBody @Valid CreateUserRequest toBeCreated,
            @AuthenticationPrincipal AuthenticatedUser manager) {
        if (userService.doesUserExist(toBeCreated.getEmail())) {
            try {
                userService.addManagerRole(toBeCreated.getUsername(), userService.getUser(manager.getId()));
                return new ResponseEntity<>("Manager role added to existing user", HttpStatus.OK);
            } catch (Exception e) {
                logger.error("Errore durante l'aggiunta del ruolo manager", e);
                return new ResponseEntity<>("Errore durante l'aggiunta del ruolo manager: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            try {
                String generatedPassword = RandomPasswordGenerator.generateRandom();
                SignupRequest signupRequest = new SignupRequest(toBeCreated, generatedPassword);
                userService.createManager(signupRequest, userService.getUser(manager.getId()));
                emailService.sendManagerAccountCreatedEmail(signupRequest.getEmail(), signupRequest.getPassword());
                return new ResponseEntity<>("Manager created", HttpStatus.CREATED);
            } catch (Exception e) {
                logger.error("Errore durante la creazione del manager", e);
                return new ResponseEntity<>("Errore durante la creazione del manager: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @PostMapping("/manager/change_password")
    public ResponseEntity<Object> changeUserPassword(@RequestBody @Valid ChangePasswordRequest authRequest) {
        String username = userService.getUsernameFromEmail(authRequest.getEmail());
        if (username == null || username.isEmpty()) {
            return new ResponseEntity<>("Utente non trovato", HttpStatus.NOT_FOUND);
        } else if (userService.getUserByUsername(username) == null
                || !userService.getUserByUsername(username).isManager()) {
            return new ResponseEntity<>("Solo i manager possono cambiare la password", HttpStatus.FORBIDDEN);
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, authRequest.getOldPassword()));

        String newPassword = authRequest.getNewPassword();
        try {
            userService.changePassword(authRequest.getEmail(), newPassword);
            return ResponseEntity.ok("Password cambiata con successo");
        } catch (Exception e) {
            logger.error("Errore durante il cambio della password", e);
            return new ResponseEntity<>("Errore durante il cambio della password", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}