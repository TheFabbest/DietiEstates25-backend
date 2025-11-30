package com.dieti.dietiestatesbackend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
  
import com.dieti.dietiestatesbackend.dto.request.CreateOfferRequest;
import com.dieti.dietiestatesbackend.dto.response.OfferResponseDTO;
import com.dieti.dietiestatesbackend.entities.Offer;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.enums.OfferStatus;
import com.dieti.dietiestatesbackend.exception.EntityNotFoundException;
import com.dieti.dietiestatesbackend.repositories.OfferRepository;
import com.dieti.dietiestatesbackend.repositories.PropertyRepository;
import com.dieti.dietiestatesbackend.repositories.UserRepository;
 
@Service
@Transactional
public class OfferService {

    private final OfferRepository offerRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    public static final String OFFER_NOT_FOUND_MSG = "Offer not found with id: ";

    @Autowired
    public OfferService(OfferRepository offerRepository, PropertyRepository propertyRepository, UserRepository userRepository) {
        this.offerRepository = offerRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    public Offer getOffer(Long id) {
        return offerRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(OFFER_NOT_FOUND_MSG + id));
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
        Property property = propertyRepository.findById(request.getPropertyId())
            .orElseThrow(() -> new EntityNotFoundException("Property not found with id: " + request.getPropertyId()));
        User user = userRepository.findById(userID)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userID));

        Offer offer = offerRepository.findByPropertyIdAndUserId(request.getPropertyId(), userID)
            .orElse(new Offer());
        if (offer.getStatus() == OfferStatus.ACCEPTED) {
            throw new IllegalStateException("The agent has already accepted a previous offer");
        }
        offer.setProperty(property);
        offer.setUser(user);
        offer.setPrice(BigDecimal.valueOf(request.getPrice()));
        offer.setStatus(OfferStatus.PENDING);
        return offerRepository.save(offer);
    }

    public Offer withdrawOffer(Long propertyID, Long userID) {
        Offer offer = offerRepository.findByPropertyIdAndUserId(propertyID, userID)
            .orElseThrow(() -> new EntityNotFoundException("Offer not found for property ID: " + propertyID + " and user ID: " + userID));
        if (offer.getStatus() != OfferStatus.PENDING) {
            throw new IllegalStateException("Only pending offers can be withdrawn");
        }
        offer.setStatus(OfferStatus.WITHDRAWN);
        return offerRepository.save(offer);
    }

    public Offer acceptOffer(Long offerID) {
        Offer offer = offerRepository.findById(offerID)
            .orElseThrow(() -> new EntityNotFoundException(OFFER_NOT_FOUND_MSG + offerID));
        if (offer.getStatus() != OfferStatus.PENDING) {
            throw new IllegalStateException("Only pending offers can be accepted");
        }
        offer.setStatus(OfferStatus.ACCEPTED);
        return offerRepository.save(offer);
    }

    public Offer rejectOffer(Long offerID) {
        Offer offer = offerRepository.findById(offerID)
            .orElseThrow(() -> new EntityNotFoundException(OFFER_NOT_FOUND_MSG + offerID));
        if (offer.getStatus() != OfferStatus.PENDING) {
            throw new IllegalStateException("Only pending offers can be rejected");
        }
        offer.setStatus(OfferStatus.REJECTED);
        return offerRepository.save(offer);
    }

    public Offer counterOffer(Long offerID, Double newPrice) {
        Offer offer = offerRepository.findByIdWithUser(offerID)
            .orElseThrow(() -> new EntityNotFoundException(OFFER_NOT_FOUND_MSG + offerID));
        if (newPrice <= 0) {
            throw new IllegalArgumentException("New price must be positive");
        }
        else if (offer.getStatus() != OfferStatus.PENDING) {
            throw new IllegalStateException("Only pending offers can be countered");
        }
        else if (offer.getPrice().compareTo(BigDecimal.valueOf(newPrice)) >= 0) {
            throw new IllegalArgumentException("Counter offer price must be higher than the original offer price");
        }
        else if (offer.getProperty().getPrice().compareTo(BigDecimal.valueOf(newPrice)) < 0) {
            throw new IllegalArgumentException("Counter offer price cannot exceed the property's asking price");
        }
        offer.setPrice(BigDecimal.valueOf(newPrice));
        offer.setStatus(OfferStatus.COUNTERED);
        return offerRepository.save(offer);
    }

    public List<Offer> getUserOffers(Long userID) {
        return offerRepository.findByUserId(userID);
    }
}