package com.dieti.dietiestatesbackend.security.permissions;

import com.dieti.dietiestatesbackend.security.AppPrincipal;

/**
 * Interfaccia per le autorizzazioni legate agli agenti.
 * Le implementazioni devono usare il service layer per recuperare i dati necessari.
 */
public interface AgentPermissionService {

    /**
     * Verifica se il principal è l'agente con id agentId oppure è manager.
     */
    boolean isAgentOrManager(AppPrincipal principal, Long agentId);

    /**
     * Permette di verificare se il principal può visualizzare entità correlate all'agente.
     */
    boolean canViewAgentRelatedEntities(AppPrincipal principal, Long agentId);

    /**
     * Verifica se il principal può gestire lo slot di disponibilità identificato da availabilityId.
     */
    boolean canManageAgentAvailability(AppPrincipal principal, Long availabilityId);
}