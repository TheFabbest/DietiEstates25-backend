package com.dieti.dietiestatesbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
  
import com.dieti.dietiestatesbackend.entities.Offer;
import com.dieti.dietiestatesbackend.repositories.OfferRepository;
import com.dieti.dietiestatesbackend.exception.EntityNotFoundException;
import com.dieti.dietiestatesbackend.dto.response.OfferResponseDTO;
 
@Service
@Transactional
public class OfferService {

    private final OfferRepository offerRepository;

    @Autowired
    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
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
        dto.setDate(offer.getDate());
        dto.setStatus(offer.getStatus());
        dto.setCreatedAt(offer.getCreatedAt());
        return dto;
    }
}