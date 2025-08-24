package com.dieti.dietiestatesbackend.service;
 
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import com.dieti.dietiestatesbackend.entities.Offer;
import com.dieti.dietiestatesbackend.repositories.OfferRepository;
import com.dieti.dietiestatesbackend.exception.EntityNotFoundException;
 
@Service
@Transactional
public class OfferService {
    private static final Logger logger = LoggerFactory.getLogger(OfferService.class);

    private final OfferRepository offerRepository;

    @Autowired
    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    public Offer getOffer(Long id) {
        return offerRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Offer not found with id: " + id));
    }

    public List<Offer> getAgentOffers(Long agentId) {
        return offerRepository.getAgentOffers(agentId);
    }
}