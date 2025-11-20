package com.dieti.dietiestatesbackend.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.dieti.dietiestatesbackend.entities.Offer;
import com.dieti.dietiestatesbackend.security.AppPrincipal;
import com.dieti.dietiestatesbackend.service.OfferService;
import com.dieti.dietiestatesbackend.service.emails.EmailService;
import com.dieti.dietiestatesbackend.dto.request.CreateOfferRequest;
import com.dieti.dietiestatesbackend.dto.response.OfferResponseDTO;
 
@RestController
public class OfferController {
    private final OfferService offerService;
    private final EmailService emailService;

    @Autowired
    public OfferController(OfferService offerService, EmailService emailService) {
        this.offerService = offerService;
        this.emailService = emailService;
    }

    @GetMapping("/offers/agent_offers/{agentID}")
    @PreAuthorize("@securityUtil.canViewAgentRelatedEntities(#agentID)")
    public ResponseEntity<Page<OfferResponseDTO>> getAgentOffers(@PathVariable("agentID") Long agentID, Pageable pageable) {
        Page<Offer> offers = offerService.getAgentOffers(agentID, pageable);
        Page<OfferResponseDTO> responseDTOs = offers.map(offerService::mapToResponseDTO);
        return ResponseEntity.ok(responseDTOs);
    }

    @PostMapping("/offers/create")
    public ResponseEntity<OfferResponseDTO> createOffer(@AuthenticationPrincipal AppPrincipal principal, @RequestBody CreateOfferRequest request) throws IOException {
        Offer offer = offerService.createOffer(request, principal.getId());
        OfferResponseDTO responseDTO = offerService.mapToResponseDTO(offer);
        emailService.sendOfferCreatedEmail(offer);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/offers/withdraw/{propertyID}")
    public ResponseEntity<OfferResponseDTO> withdrawOffer(@AuthenticationPrincipal AppPrincipal principal, @PathVariable Long propertyID) throws IOException {
        Offer offer = offerService.withdrawOffer(propertyID, principal.getId());
        OfferResponseDTO responseDTO = offerService.mapToResponseDTO(offer);
        emailService.sendOfferWithdrawnEmail(offer);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/offers/accept/{offerID}")
    @PreAuthorize("@securityUtil.isAgentOfOffer(principal, #offerID)")
    public ResponseEntity<OfferResponseDTO> acceptOffer(@AuthenticationPrincipal AppPrincipal principal, @PathVariable Long offerID) throws IOException {
        Offer offer = offerService.acceptOffer(offerID, principal.getId());
        OfferResponseDTO responseDTO = offerService.mapToResponseDTO(offer);
        emailService.sendOfferAcceptedEmail(offer);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/offers/reject/{offerID}")
    @PreAuthorize("@securityUtil.isAgentOfOffer(principal, #offerID)")
    public ResponseEntity<OfferResponseDTO> rejectOffer(@AuthenticationPrincipal AppPrincipal principal, @PathVariable Long offerID) throws IOException {
        Offer offer = offerService.rejectOffer(offerID, principal.getId());
        OfferResponseDTO responseDTO = offerService.mapToResponseDTO(offer);
        emailService.sendOfferRejectedEmail(offer);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/offers/counter/{offerID}")
    @PreAuthorize("@securityUtil.isAgentOfOffer(principal, #offerID)")
    public ResponseEntity<OfferResponseDTO> counterOffer(@AuthenticationPrincipal AppPrincipal principal, @PathVariable Long offerID, @RequestBody Double newPrice) throws IOException {
        Offer offer = offerService.counterOffer(offerID, principal.getId(), newPrice);
        OfferResponseDTO responseDTO = offerService.mapToResponseDTO(offer);
        emailService.sendOfferCountered(offer);
        return ResponseEntity.ok(responseDTO);
    }
}