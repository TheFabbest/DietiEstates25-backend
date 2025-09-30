package com.dieti.dietiestatesbackend.service;

import com.dieti.dietiestatesbackend.entities.AgentAvailability;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service per la gestione degli slot di disponibilità degli agenti.
 * Espone operazioni usate sia dal controller che da AgentLookupServiceImpl.
 */
public interface AgentAvailabilityService {

    AgentAvailability create(AgentAvailability availability);

    List<AgentAvailability> getAvailabilitiesForAgent(Long agentId);

    Optional<AgentAvailability> findById(Long id);

    void deleteById(Long id);

    /**
     * Controlla se esiste almeno uno slot dell'agente che copre interamente l'intervallo [start,end)
     */
    boolean isSlotCoveringInterval(Long agentId, Instant start, Instant end);
}