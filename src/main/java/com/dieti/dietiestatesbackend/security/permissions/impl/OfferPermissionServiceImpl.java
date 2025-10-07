package com.dieti.dietiestatesbackend.security.permissions.impl;

import com.dieti.dietiestatesbackend.entities.Offer;
import com.dieti.dietiestatesbackend.security.AppPrincipal;
import com.dieti.dietiestatesbackend.security.permissions.OfferPermissionService;
import com.dieti.dietiestatesbackend.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementazione delle regole di autorizzazione per le offerte.
 * Usa il livello di business (OfferService) per recuperare i dati necessari.
 */
@Component
@RequiredArgsConstructor
public class OfferPermissionServiceImpl implements OfferPermissionService {

    private static final Logger logger = LoggerFactory.getLogger(OfferPermissionServiceImpl.class);

    private final OfferService offerService;

    @Override
    @Transactional(readOnly = true)
    public boolean isAgentOfOffer(AppPrincipal principal, Long offerId) {
        logger.debug("isAgentOfOffer - principal: {}, offerId: {}", principal, offerId);
        if (principal == null || principal.getId() == null || offerId == null) return false;
        if (principal.isManager()) return true;

        try {
            Offer offer = offerService.getOffer(offerId);
            if (offer == null || offer.getProperty() == null || offer.getProperty().getAgent() == null) {
                return false;
            }
            return principal.getId().equals(offer.getProperty().getAgent().getId());
        } catch (Exception e) {
            logger.debug("isAgentOfOffer - error fetching offer {}: {}", offerId, e.getMessage());
            return false;
        }
    }
}