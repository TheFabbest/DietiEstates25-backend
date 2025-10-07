package com.dieti.dietiestatesbackend.security.permissions.impl;

import com.dieti.dietiestatesbackend.entities.AgentAvailability;
import com.dieti.dietiestatesbackend.security.AppPrincipal;
import com.dieti.dietiestatesbackend.security.permissions.AgentPermissionService;
import com.dieti.dietiestatesbackend.service.AgentAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgentPermissionServiceImpl implements AgentPermissionService {

    private static final Logger logger = LoggerFactory.getLogger(AgentPermissionServiceImpl.class);

    private final AgentAvailabilityService agentAvailabilityService;

    @Override
    public boolean isAgentOrManager(AppPrincipal principal, Long agentId) {
        logger.debug("isAgentOrManager - principal: {}, agentId: {}", principal, agentId);
        if (principal == null || agentId == null) return false;
        if (principal.isManager()) return true;
        return principal.getId() != null && principal.getId().equals(agentId);
    }

    @Override
    public boolean canViewAgentRelatedEntities(AppPrincipal principal, Long agentId) {
        // Reuse same semantics: agent itself or manager
        return isAgentOrManager(principal, agentId);
    }

    @Override
    public boolean canManageAgentAvailability(AppPrincipal principal, Long availabilityId) {
        logger.debug("canManageAgentAvailability - principal: {}, availabilityId: {}", principal, availabilityId);
        if (principal == null || principal.getId() == null || availabilityId == null) return false;
        if (principal.isManager()) return true;
        return agentAvailabilityService.findById(availabilityId)
                .map(AgentAvailability::getAgent)
                .map(a -> a != null && principal.getId().equals(a.getId()))
                .orElse(false);
    }
}