package com.dieti.dietiestatesbackend.controller;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.dieti.dietiestatesbackend.service.OfferService;
 
@RestController
public class OfferController {
    private static final Logger logger = LoggerFactory.getLogger(OfferController.class);
    private final OfferService offerService;

    @Autowired
    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping("/offers/agent_offers/{id}")
    @PreAuthorize("@securityUtil.canAccessOffersForAgent(#authentication.principal, #agentID)")
    public ResponseEntity<Object> getAgentOffers(@PathVariable("id") Long agentID) {
        return ResponseEntity.ok(offerService.getAgentOffers(agentID));
    }
}