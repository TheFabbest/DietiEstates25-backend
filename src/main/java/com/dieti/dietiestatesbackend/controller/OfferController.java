package com.dieti.dietiestatesbackend.controller;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.dieti.dietiestatesbackend.entities.Offer;
import com.dieti.dietiestatesbackend.service.OfferService;
 
@RestController
public class OfferController {
    private static final Logger logger = LoggerFactory.getLogger(OfferController.class);
    private final OfferService offerService;

    @Autowired
    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping("/offers/agent_offers/{agentID}")
    @PreAuthorize("@securityUtil.canViewAgentRelatedEntities(#agentID)")
    public ResponseEntity<Page<Offer>> getAgentOffers(@PathVariable("agentID") Long agentID, Pageable pageable) {
        Page<Offer> offers = offerService.getAgentOffers(agentID, pageable);
        return ResponseEntity.ok(offers);
    }
}