package com.dieti.dietiestatesbackend.security.permissions.impl;

import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.security.AppPrincipal;
import com.dieti.dietiestatesbackend.security.permissions.PropertyPermissionService;
import com.dieti.dietiestatesbackend.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implementazione delle regole di autorizzazione per le Property.
 * Usa il PropertyService per recuperare i dati di business (mai i repository).
 */
@Component
@RequiredArgsConstructor
public class PropertyPermissionServiceImpl implements PropertyPermissionService {

    private static final Logger logger = LoggerFactory.getLogger(PropertyPermissionServiceImpl.class);

    private final PropertyService propertyService;

    @Override
    public boolean canAccessProperty(AppPrincipal principal, Long propertyId) {
        logger.debug("PropertyPermissionServiceImpl.canAccessProperty - principal: {}, propertyId: {}", principal, propertyId);
        if (principal == null || propertyId == null) return false;
        if (principal.isManager()) return true;

        try {
            Property property = propertyService.getProperty(propertyId);
            if (property == null || property.getAgent() == null) return false;
            return principal.getId() != null && principal.getId().equals(property.getAgent().getId());
        } catch (Exception e) {
            logger.debug("canAccessProperty - errore recuperando property {}: {}", propertyId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean canViewPropertyVisits(AppPrincipal principal, Long propertyId) {
        // Stessa regola di accesso alla property per vedere le visite
        return canAccessProperty(principal, propertyId);
    }

    @Override
    public boolean isAgentOfProperty(AppPrincipal principal, Long propertyId) {
        logger.debug("PropertyPermissionServiceImpl.isAgentOfProperty - principal: {}, propertyId: {}", principal, propertyId);
        if (principal == null || propertyId == null) return false;

        try {
            Property property = propertyService.getProperty(propertyId);
            if (property == null || property.getAgent() == null) return false;
            return principal.getId() != null && principal.getId().equals(property.getAgent().getId());
        } catch (Exception e) {
            logger.debug("isAgentOfProperty - errore recuperando property {}: {}", propertyId, e.getMessage());
            return false;
        }
    }
}