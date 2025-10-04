package com.dieti.dietiestatesbackend.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
  
import com.dieti.dietiestatesbackend.entities.Offer;
import com.dieti.dietiestatesbackend.enums.OfferStatus;
import com.dieti.dietiestatesbackend.repositories.OfferRepository;
import com.dieti.dietiestatesbackend.repositories.PropertyRepository;
import com.dieti.dietiestatesbackend.repositories.UserRepository;
import com.dieti.dietiestatesbackend.exception.EntityNotFoundException;
import com.dieti.dietiestatesbackend.dto.request.CreateOfferRequest;
import com.dieti.dietiestatesbackend.dto.response.OfferResponseDTO;
 
@Service
@Transactional
public class OfferService {

    private final OfferRepository offerRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    @Autowired
    public OfferService(OfferRepository offerRepository, PropertyRepository propertyRepository, UserRepository userRepository) {
        this.offerRepository = offerRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    public Offer getOffer(Long id) {
        return offerRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Offer not found with id: " + id));
    }

    public Page<Offer> getAgentOffers(Long agentId, Pageable pageable) {
        return offerRepository.getAgentOffers(agentId, pageable);
    }

    /**
     * Mappa un'entità Offer a OfferResponseDTO
     */
    public OfferResponseDTO mapToResponseDTO(Offer offer) {
        OfferResponseDTO dto = new OfferResponseDTO();
        dto.setId(offer.getId());
        dto.setProperty(null); // Property verrà mappato separatamente se necessario
        dto.setUser(null); // User verrà mappato separatamente se necessario
        dto.setPrice(offer.getPrice());
        dto.setStatus(offer.getStatus());
        dto.setCreatedAt(offer.getCreatedAt());
        return dto;
    }

    public Offer createOffer(CreateOfferRequest request, Long userID) {
        Offer offer = new Offer();
        offer.setProperty(propertyRepository.getReferenceById(request.getPropertyId()));
        offer.setUser(userRepository.getReferenceById(userID));
        offer.setPrice(BigDecimal.valueOf(request.getPrice()));
        offer.setStatus(OfferStatus.PENDING);
        return offerRepository.save(offer);
    }

    public Offer withdrawOffer(Long propertyID, Long userID) {
        Offer offer = offerRepository.findByPropertyIdAndUserId(propertyID, userID)
            .orElseThrow(() -> new EntityNotFoundException("Offer not found for property ID: " + propertyID + " and user ID: " + userID));
        offer.setStatus(OfferStatus.WITHDRAWN);
        return offerRepository.save(offer);
    }

    public Offer acceptOffer(Long offerID, Long agentID) {
        Offer offer = offerRepository.findById(offerID)
            .orElseThrow(() -> new EntityNotFoundException("Offer not found with id: " + offerID));
        offer.setStatus(OfferStatus.ACCEPTED);
        return offerRepository.save(offer);
    }

    public Offer rejectOffer(Long offerID, Long agentID) {
        Offer offer = offerRepository.findById(offerID)
            .orElseThrow(() -> new EntityNotFoundException("Offer not found with id: " + offerID));
        offer.setStatus(OfferStatus.REJECTED);
        return offerRepository.save(offer);
    }

    public Offer counterOffer(Long offerID, Long agentID, Double newPrice) {
        Offer offer = offerRepository.findById(offerID)
            .orElseThrow(() -> new EntityNotFoundException("Offer not found with id: " + offerID));
        if (newPrice <= 0) {
            throw new IllegalArgumentException("New price must be positive");
        }
        else if (offer.getStatus() != OfferStatus.PENDING) {
            throw new IllegalStateException("Only pending offers can be countered");
        }
        else if (offer.getPrice().compareTo(BigDecimal.valueOf(newPrice)) <= 0) {
            throw new IllegalArgumentException("Counter offer price must be lower than the original offer price");
        }
        offer.setPrice(BigDecimal.valueOf(newPrice));
        offer.setStatus(OfferStatus.COUNTERED);
        return offerRepository.save(offer);
    }
}