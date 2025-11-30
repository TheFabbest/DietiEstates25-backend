package com.dieti.dietiestatesbackend.security;

import org.springframework.stereotype.Component;

import com.dieti.dietiestatesbackend.dto.request.VisitCreationRequestDTO;
import com.dieti.dietiestatesbackend.enums.VisitStatus;
import com.dieti.dietiestatesbackend.security.permissions.PermissionFacade;

import lombok.RequiredArgsConstructor;

/**
 * Utility bean esposto per SpEL nelle espressioni @PreAuthorize.
 * Questa versione delega tutta la logica decisionale a PermissionFacade per rispettare
 * l'architettura proposta (nessun accesso diretto ai repository da qui).
 */
@Component("securityUtil")
@RequiredArgsConstructor
public class SecurityUtil {

    private final PermissionFacade permissionFacade;

    public boolean canAccessProperty(AppPrincipal principal, Long propertyId) {
        return permissionFacade.canAccessProperty(principal, propertyId);
    }

    public boolean canViewPropertyVisits(AppPrincipal principal, Long propertyId) {
        return permissionFacade.canViewPropertyVisits(principal, propertyId);
    }

    public boolean isAgentOrManager(AppPrincipal principal, Long agentId) {
        return permissionFacade.isAgentOrManager(principal, agentId);
    }

    public boolean canViewAgentRelatedEntities(AppPrincipal principal, Long agentId) {
        return permissionFacade.canViewAgentRelatedEntities(principal, agentId);
    }

    public boolean canManageAgentAvailability(AppPrincipal principal, Long availabilityId) {
        return permissionFacade.canManageAgentAvailability(principal, availabilityId);
    }

    public boolean canCreateVisit(AppPrincipal principal, VisitCreationRequestDTO request) {
        return permissionFacade.canCreateVisit(principal, request);
    }

    public boolean canUpdateVisitStatus(AppPrincipal principal, Long visitId, VisitStatus newStatus) {
        return permissionFacade.canUpdateVisitStatus(principal, visitId, newStatus);
    }

    public boolean canCancelVisit(AppPrincipal principal, Long visitId) {
        return permissionFacade.canCancelVisit(principal, visitId);
    }

    public boolean isAgentOfOffer(AppPrincipal principal, Long offerID) {
        return permissionFacade.isAgentOfOffer(principal, offerID);
    }

    public boolean canAccessContract(AppPrincipal principal, Long contractId) {
        return permissionFacade.canAccessContract(principal, contractId);
    }

    public boolean isAgentOfProperty(AppPrincipal principal, Long propertyId) {
        return permissionFacade.isAgentOfProperty(principal, propertyId);
    }
}