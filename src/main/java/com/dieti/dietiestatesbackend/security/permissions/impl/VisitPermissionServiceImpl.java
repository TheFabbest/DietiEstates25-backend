package com.dieti.dietiestatesbackend.security.permissions.impl;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dieti.dietiestatesbackend.dto.request.VisitCreationRequestDTO;
import com.dieti.dietiestatesbackend.entities.Visit;
import com.dieti.dietiestatesbackend.enums.VisitStatus;
import com.dieti.dietiestatesbackend.security.AppPrincipal;
import com.dieti.dietiestatesbackend.security.permissions.VisitPermissionService;
import com.dieti.dietiestatesbackend.service.VisitService;

import lombok.RequiredArgsConstructor;

/**
 * Implementazione delle regole di autorizzazione per le visite.
 * Usa il livello di business (VisitService) per recuperare i dati necessari.
 */
@Component
@RequiredArgsConstructor
public class VisitPermissionServiceImpl implements VisitPermissionService {

    private static final Logger logger = LoggerFactory.getLogger(VisitPermissionServiceImpl.class);

    private final VisitService visitService;

    @Override
    public boolean canCreateVisit(AppPrincipal principal, VisitCreationRequestDTO request) {
        logger.debug("canCreateVisit - principal: {}, request: {}", principal, request);
        // Tutti gli utenti autenticati possono creare una visita
        return !(principal == null || request == null);
    }

    @Override
    public boolean canUpdateVisitStatus(AppPrincipal principal, Long visitId, VisitStatus newStatus) {
        logger.debug("canUpdateVisitStatus - principal: {}, visitId: {}, newStatus: {}", principal, visitId, newStatus);
        if (principal == null || principal.getId() == null || visitId == null) return false;
        if (principal.isManager()) return true;
        try {
            Visit visit = visitService.getVisit(visitId);
            if (newStatus == VisitStatus.CONFIRMED || newStatus == VisitStatus.REJECTED) {
                Long agentId = visit.getAgent() != null ? visit.getAgent().getId() : null;
                return principal.getId().equals(agentId);
            }
            if (newStatus == VisitStatus.CANCELLED) {
                return canCancelVisit(principal, visitId);
            }
            return false;
        } catch (Exception e) {
            logger.debug("canUpdateVisitStatus - error fetching visit {}: {}", visitId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean canCancelVisit(AppPrincipal principal, Long visitId) {
        logger.debug("canCancelVisit - principal: {}, visitId: {}", principal, visitId);
        if (principal == null || principal.getId() == null || visitId == null) return false;
        if (principal.isManager()) return true;
        try {
            Visit visit = visitService.getVisit(visitId);
            Long principalId = principal.getId();
            Long agentId = visit.getAgent() != null ? visit.getAgent().getId() : null;
            Long userId = visit.getUser() != null ? visit.getUser().getId() : null;
            logger.debug("canCancelVisit - principalId: {}, agentId: {}, userId: {}", principalId, agentId, userId);
            if (principalId.equals(agentId)) return true;
            if (principalId.equals(userId)) {
                VisitStatus status = visit.getStatus();
                if (status == VisitStatus.PENDING) return true;
                if (status == VisitStatus.CONFIRMED) {
                    Instant start = visit.getStartTime();
                    if (start == null) return false;
                    Instant cutoff = start.minusSeconds(24L * 3600);
                    return Instant.now().isBefore(cutoff);
                }
            }
            return false;
        } catch (Exception e) {
            logger.debug("canCancelVisit - error fetching visit {}: {}", visitId, e.getMessage());
            return false;
        }
    }
}