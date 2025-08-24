package com.dieti.dietiestatesbackend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.dieti.dietiestatesbackend.dto.response.UserResponse;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.service.UserService;

@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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
}