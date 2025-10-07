package com.dieti.dietiestatesbackend.security.permissions.impl;

import com.dieti.dietiestatesbackend.security.AppPrincipal;
import com.dieti.dietiestatesbackend.security.permissions.ContractPermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

/**
 * Implementazione minimale per i permessi sui contratti.
 * Al momento la regola è: solo i manager possono accedere ai contratti.
 * Questa implementazione utilizza l'AppPrincipal e non accede ai repository.
 */
@Component
@RequiredArgsConstructor
public class ContractPermissionServiceImpl implements ContractPermissionService {

    private static final Logger logger = LoggerFactory.getLogger(ContractPermissionServiceImpl.class);

    @Override
    public boolean canAccessContract(AppPrincipal principal, Long contractId) {
        logger.debug("canAccessContract - principal: {}, contractId: {}", principal, contractId);
        if (principal == null) return false;
        return principal.isManager();
    }
}