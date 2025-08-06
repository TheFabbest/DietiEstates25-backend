package com.dieti.dietiestatesbackend.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.dieti.dietiestatesbackend.dto.Listing;
import com.dieti.dietiestatesbackend.security.AccessTokenProvider;
import com.dieti.dietiestatesbackend.service.AddressService;

@RestController
public class AddressController {
    private static final Logger logger = Logger.getLogger(AddressController.class.getName());
    
    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/address/{id}")
    public ResponseEntity<Object> getAddress(
            @PathVariable("id") Long id,
            @RequestHeader(value = "Bearer", required = false) String accessToken) {
        if (accessToken == null || !AccessTokenProvider.validateToken(accessToken)) {
            return new ResponseEntity<>("Token non valido o scaduto", HttpStatusCode.valueOf(498));
        }
        try {
            return ResponseEntity.ok(addressService.getAddress(id));
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante il recupero dell'indirizzo: {0}", e.getMessage());
            return ResponseEntity.internalServerError().body(new ArrayList<Listing>());
        }
    }
}