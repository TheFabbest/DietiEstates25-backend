package com.dieti.dietiestatesbackend.security;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.repositories.PropertyRepository;

/**
 * Utility bean esposto per SpEL nelle espressioni @PreAuthorize.
 * Fornisce metodi per verificare ownership/permessi a livello di risorsa.
 */
@Component("securityUtil")
public class SecurityUtil {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    private final PropertyRepository propertyRepository;

    @Autowired
    public SecurityUtil(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    /**
     * Verifica se l'utente autenticato può accedere ai dettagli di una proprietà.
     * Condizioni: è manager oppure è l'agente associato alla proprietà.
     */
    public boolean canAccessProperty(AppPrincipal principal, Long propertyId) {
        logger.debug("canAccessProperty - principal: {}, propertyId: {}", principal, propertyId);
        if (principal == null) {
            logger.debug("canAccessProperty - principal è null -> false");
            return false;
        }
        logger.debug("canAccessProperty - principal id: {}, isManager: {}", principal.getId(), principal.isManager());
        if (principal.isManager()) {
            logger.debug("canAccessProperty - principal è manager -> true");
            return true;
        }
        if (propertyId == null) {
            logger.debug("canAccessProperty - propertyId è null -> false");
            return false;
        }
 
        Optional<Property> prop = propertyRepository.findById(propertyId);
        if (!prop.isPresent()) {
            logger.debug("canAccessProperty - property non trovata (id={}) -> false", propertyId);
            return false;
        }
 
        Property p = prop.get();
        Long agentId = p.getAgent() != null ? p.getAgent().getId() : null;
        boolean match = agentId != null && agentId.equals(principal.getId());
        logger.debug("canAccessProperty - property.agentId: {}, principal.id: {}, match: {}", agentId, principal.getId(), match);
        return match;
    }

    /**
     * Verifica se l'utente è manager oppure corrisponde all'agentId passato.
     */
    public boolean isAgentOrManager(AppPrincipal principal, Long agentId) {
        logger.debug("isAgentOrManager - principal: {}, agentId: {}", principal, agentId);
        if (principal == null) {
            logger.debug("isAgentOrManager - principal è null -> false");
            return false;
        }
        logger.debug("isAgentOrManager - principal id: {}, isManager: {}", principal.getId(), principal.isManager());
        if (principal.isManager()) {
            logger.debug("isAgentOrManager - principal è manager -> true");
            return true;
        }
        if (agentId == null) {
            logger.debug("isAgentOrManager - agentId è null -> false");
            return false;
        }
        boolean result = principal.getId() != null && principal.getId().equals(agentId);
        logger.debug("isAgentOrManager - confronto principal.id == agentId ? {}", result);
        return result;
    }

    /**
     * Alias per offerte: solo manager o l'agente specificato possono accedere.
     */
    public boolean canAccessOffersForAgent(AppPrincipal principal, Long agentId) {
        boolean result = isAgentOrManager(principal, agentId);
        logger.debug("canAccessOffersForAgent - result: {}", result);
        return result;
    }

    /**
     * Alias per visite: solo manager o l'agente specificato possono accedere.
     */
    public boolean canAccessVisitsForAgent(AppPrincipal principal, Long agentId) {
        boolean result = isAgentOrManager(principal, agentId);
        logger.debug("canAccessVisitsForAgent - result: {}", result);
        return result;
    }

    /**
     * Controllo per i contratti: al momento l'entità Contract non espone un owner diretto;
     * quindi per sicurezza permettiamo solo i manager.
     */
    public boolean canAccessContract(AppPrincipal principal, Long contractId) {
        logger.debug("canAccessContract - principal: {}, contractId: {}", principal, contractId);
        if (principal == null) {
            logger.debug("canAccessContract - principal è null -> false");
            return false;
        }
        boolean isManager = principal.isManager();
        logger.debug("canAccessContract - principal id: {}, isManager: {}", principal.getId(), isManager);
        return isManager;
    }

}