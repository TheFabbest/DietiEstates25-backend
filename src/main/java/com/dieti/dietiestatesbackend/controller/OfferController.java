package com.dieti.dietiestatesbackend.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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

import com.dieti.dietiestatesbackend.dto.request.CreateOfferRequest;
import com.dieti.dietiestatesbackend.dto.response.OfferResponseDTO;
import com.dieti.dietiestatesbackend.dto.response.UserResponse;
import com.dieti.dietiestatesbackend.entities.Offer;
import com.dieti.dietiestatesbackend.mappers.MapStructPropertyMapper;
import com.dieti.dietiestatesbackend.security.AppPrincipal;
import com.dieti.dietiestatesbackend.service.OfferService;
import com.dieti.dietiestatesbackend.service.emails.EmailService;
 
@RestController
public class OfferController {
    private final OfferService offerService;
    private final EmailService emailService;
    private final MapStructPropertyMapper defaultMapper;

    @Autowired
    public OfferController(OfferService offerService, EmailService emailService, MapStructPropertyMapper defaultMapper) {
        this.offerService = offerService;
        this.emailService = emailService;
        this.defaultMapper = defaultMapper;
    }

    @GetMapping("/offers/agent_offers")
    @PreAuthorize("@securityUtil.canViewAgentRelatedEntities(principal, principal.id)")
    public ResponseEntity<Page<OfferResponseDTO>> getAgentOffers(@AuthenticationPrincipal AppPrincipal principal, Pageable pageable) {
        Page<Offer> offers = offerService.getAgentOffers(principal.getId(), pageable);
        Page<OfferResponseDTO> responseDTOs = offers.map(offer -> {
            OfferResponseDTO dto = offerService.mapToResponseDTO(offer);
            dto.setProperty(defaultMapper.propertyToPropertyResponse(offer.getProperty()));
            UserResponse userResponse = new UserResponse(offer.getUser());
            dto.setUser(userResponse);
            return dto;
        });
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
        emailService.sendOfferWithdrawnEmail(offer);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/offers/accept/{offerID}")
    @PreAuthorize("@securityUtil.isAgentOfOffer(principal, #offerID)")
    public ResponseEntity<Void> acceptOffer(@AuthenticationPrincipal AppPrincipal principal, @PathVariable Long offerID) throws IOException {
        Offer offer = offerService.acceptOffer(offerID);
        emailService.sendOfferAcceptedEmail(offer);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/offers/reject/{offerID}")
    @PreAuthorize("@securityUtil.isAgentOfOffer(principal, #offerID)")
    public ResponseEntity<Void> rejectOffer(@AuthenticationPrincipal AppPrincipal principal, @PathVariable Long offerID) throws IOException {
        Offer offer = offerService.rejectOffer(offerID);
        emailService.sendOfferRejectedEmail(offer);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/offers/counter/{offerID}")
    @PreAuthorize("@securityUtil.isAgentOfOffer(principal, #offerID)")
    public ResponseEntity<Void> counterOffer(@AuthenticationPrincipal AppPrincipal principal, @PathVariable Long offerID, @RequestBody Double newPrice) throws IOException {
        Offer offer = offerService.counterOffer(offerID, newPrice);
        emailService.sendOfferCountered(offer);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/offers")
    public ResponseEntity<List<OfferResponseDTO>> getMyOffers(@AuthenticationPrincipal AppPrincipal principal) {
        List<Offer> offers = offerService.getUserOffers(principal.getId());
        List<OfferResponseDTO> responseDTOs = offers.stream()
            .map(offer -> {
                OfferResponseDTO dto = offerService.mapToResponseDTO(offer);
                dto.setProperty(defaultMapper.propertyToPropertyResponse(offer.getProperty()));
                return dto;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }
}