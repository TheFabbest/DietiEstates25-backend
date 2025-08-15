package com.dieti.dietiestatesbackend.controller;

import java.sql.SQLException;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.dieti.dietiestatesbackend.security.AccessTokenProvider;
import com.dieti.dietiestatesbackend.service.OfferService;

@RestController
public class OfferController {
    private static final Logger logger = Logger.getLogger(OfferController.class.getName());
    private final OfferService offerService;

    @Autowired
    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping("/offers/getAgentOffers/{id}")
    public ResponseEntity<Object> getAgentOffers(
            @PathVariable("id") Long agentID,
            @RequestHeader(value = "Bearer", required = true) String accessToken) throws SQLException {
        if (accessToken == null || !AccessTokenProvider.validateToken(accessToken)) {
            return new ResponseEntity<>("Token non valido o scaduto", HttpStatusCode.valueOf(498));
        }
        return ResponseEntity.ok(offerService.getAgentOffers(agentID));
    }
}