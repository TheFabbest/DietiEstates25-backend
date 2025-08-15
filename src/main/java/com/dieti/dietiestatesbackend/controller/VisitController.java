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
import com.dieti.dietiestatesbackend.service.VisitService;

@RestController
public class VisitController {
    private static final Logger logger = Logger.getLogger(VisitController.class.getName());
    private final VisitService visitService;

    @Autowired
    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @GetMapping("/offers/agent_visits/{id}")
    public ResponseEntity<Object> getAgentVisits(
            @PathVariable("id") Long agentID,
            @RequestHeader(value = "Bearer", required = true) String accessToken) throws SQLException {
        if (accessToken == null || !AccessTokenProvider.validateToken(accessToken)) {
            return new ResponseEntity<>("Token non valido o scaduto", HttpStatusCode.valueOf(498));
        }
        return ResponseEntity.ok(visitService.getAgentVisits(agentID));
    }
}