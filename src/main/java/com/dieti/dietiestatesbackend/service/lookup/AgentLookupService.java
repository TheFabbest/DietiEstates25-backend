package com.dieti.dietiestatesbackend.service.lookup;

import com.dieti.dietiestatesbackend.entities.User;
import java.util.Optional;

/**
 * Service interface responsabile del lookup degli agenti.
 * Estrae la logica di ricerca dall'Application Service per favorire testabilità e separazione delle responsabilità.
 */
public interface AgentLookupService {
    Optional<User> findAgentByUsername(String username);
}