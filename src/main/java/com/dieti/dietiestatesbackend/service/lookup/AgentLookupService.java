package com.dieti.dietiestatesbackend.service.lookup;

import com.dieti.dietiestatesbackend.entities.User;
import java.util.Optional;
import java.time.Instant;

/**
 * Service interface responsabile del lookup degli agenti.
 * Estrae la logica di ricerca dall'Application Service per favorire testabilità e separazione delle responsabilità.
 *
 * Nota: estendiamo l'interfaccia con un metodo opzionale per verificare la disponibilità
 * dichiarata dall'agente. Implementazioni legacy possono restituire sempre true.
 */
public interface AgentLookupService {
    Optional<User> findAgentByUsername(String username);

    /**
     * Verifica (se supportato) se l'agente ha dichiarato disponibilità per l'intervallo [start,end).
     * L'implementazione può scegliere di non supportare questa operazione; in tal caso può
     * restituire Optional.empty() oppure un valore di fallback.
     *
     * @param agentId id agente
     * @param start inizio slot
     * @param end fine slot
     * @return Optional<Boolean> - empty se non supportato, altrimenti true/false
     */
    default Optional<Boolean> isAgentAvailable(Long agentId, Instant start, Instant end) {
        return Optional.empty();
    }
}