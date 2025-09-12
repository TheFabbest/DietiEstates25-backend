package com.dieti.dietiestatesbackend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.dieti.dietiestatesbackend.dto.request.AuthRequest;
import com.dieti.dietiestatesbackend.dto.request.ChangePasswordRequest;
import com.dieti.dietiestatesbackend.dto.request.SignupRequest;
import com.dieti.dietiestatesbackend.dto.response.UserResponse;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.security.AuthenticatedUser;
import com.dieti.dietiestatesbackend.service.UserService;

import jakarta.validation.Valid;

@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
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
            return new ResponseEntity<>("Agente non trovato", HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping("/agent/create")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Object> createAgent(@RequestBody @Valid SignupRequest toBeCreated, @AuthenticationPrincipal AuthenticatedUser manager) {
        try {
            userService.createAgent(toBeCreated, userService.getUser(manager.getId()));
            return new ResponseEntity<>("Agent created", HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Errore durante la creazione dell'agente", e);
            return new ResponseEntity<>("Errore durante la creazione dell'agente: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/manager/change_password")
    public ResponseEntity<Object> changeUserPassword(@RequestBody @Valid ChangePasswordRequest authRequest) {
        String username = userService.getUsernameFromEmail(authRequest.getEmail());
        if (username == null || username.isEmpty()) {
            return new ResponseEntity<>("Credenziali non valide", HttpStatus.UNAUTHORIZED);
        }
        else if (userService.getUserByUsername(username) == null || !userService.getUserByUsername(username).isManager()) {
            return new ResponseEntity<>("Solo i manager possono cambiare la password", HttpStatus.FORBIDDEN);
        }

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, authRequest.getOldPassword())
        );

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