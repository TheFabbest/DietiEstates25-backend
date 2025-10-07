package com.dieti.dietiestatesbackend.security.permissions;

import com.dieti.dietiestatesbackend.security.AppPrincipal;

/**
 * Interfaccia per le autorizzazioni relative ai contratti.
 */
public interface ContractPermissionService {

    /**
     * Verifica se il principal può accedere al contratto indicato.
     */
    boolean canAccessContract(AppPrincipal principal, Long contractId);
}