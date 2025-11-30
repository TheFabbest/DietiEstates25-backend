package com.dieti.dietiestatesbackend.security.permissions.impl;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import com.dieti.dietiestatesbackend.dto.request.VisitCreationRequestDTO;
import com.dieti.dietiestatesbackend.enums.VisitStatus;
import com.dieti.dietiestatesbackend.security.AppPrincipal;
import com.dieti.dietiestatesbackend.security.permissions.AgentPermissionService;
import com.dieti.dietiestatesbackend.security.permissions.ContractPermissionService;
import com.dieti.dietiestatesbackend.security.permissions.OfferPermissionService;
import com.dieti.dietiestatesbackend.security.permissions.PermissionFacade;
import com.dieti.dietiestatesbackend.security.permissions.PropertyPermissionService;
import com.dieti.dietiestatesbackend.security.permissions.VisitPermissionService;

/**
 * Implementazione della PermissionFacade che orchestra i sottoservizi di permesso
 * e delega le decisioni a loro.
 */
@Component
@RequiredArgsConstructor
public class PermissionFacadeImpl implements PermissionFacade {

    private final PropertyPermissionService propertyPermissionService;
    private final VisitPermissionService visitPermissionService;
    private final AgentPermissionService agentPermissionService;
    private final OfferPermissionService offerPermissionService;
    private final ContractPermissionService contractPermissionService;

    @Override
    public boolean canAccessProperty(AppPrincipal principal, Long propertyId) {
        return propertyPermissionService.canAccessProperty(principal, propertyId);
    }

    @Override
    public boolean canViewPropertyVisits(AppPrincipal principal, Long propertyId) {
        return propertyPermissionService.canViewPropertyVisits(principal, propertyId);
    }

    @Override
    public boolean isAgentOrManager(AppPrincipal principal, Long agentId) {
        return agentPermissionService.isAgentOrManager(principal, agentId);
    }

    @Override
    public boolean canViewAgentRelatedEntities(AppPrincipal principal, Long agentId) {
        return agentPermissionService.canViewAgentRelatedEntities(principal, agentId);
    }

    @Override
    public boolean canManageAgentAvailability(AppPrincipal principal, Long availabilityId) {
        return agentPermissionService.canManageAgentAvailability(principal, availabilityId);
    }

    @Override
    public boolean canCreateVisit(AppPrincipal principal, VisitCreationRequestDTO request) {
        return visitPermissionService.canCreateVisit(principal, request);
    }

    @Override
    public boolean canUpdateVisitStatus(AppPrincipal principal, Long visitId, VisitStatus newStatus) {
        return visitPermissionService.canUpdateVisitStatus(principal, visitId, newStatus);
    }

    @Override
    public boolean canCancelVisit(AppPrincipal principal, Long visitId) {
        return visitPermissionService.canCancelVisit(principal, visitId);
    }

    @Override
    public boolean isAgentOfOffer(AppPrincipal principal, Long offerId) {
        return offerPermissionService.isAgentOfOffer(principal, offerId);
    }

    @Override
    public boolean canAccessContract(AppPrincipal principal, Long contractId) {
        return contractPermissionService.canAccessContract(principal, contractId);
    }

    @Override
    public boolean isAgentOfProperty(AppPrincipal principal, Long propertyId) {
        return propertyPermissionService.isAgentOfProperty(principal, propertyId);
    }
}