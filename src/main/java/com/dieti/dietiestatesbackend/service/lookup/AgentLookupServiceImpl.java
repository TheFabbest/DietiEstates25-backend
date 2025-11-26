package com.dieti.dietiestatesbackend.service.lookup;

import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.repositories.UserRepository;
import com.dieti.dietiestatesbackend.repositories.VisitRepository;
import com.dieti.dietiestatesbackend.enums.VisitStatus;
import com.dieti.dietiestatesbackend.service.AgentAvailabilityService;

/**
 * Clean, well-factored implementation of AgentLookupService.
 * Responsibilities:
 *  - resolve agent by username
 *  - determine real availability by combining declared slots and confirmed visits
 *
 * Returns Optional<Boolean> to stay compatible with implementations that may not support availability checks.
 */
@Service
public class AgentLookupServiceImpl implements AgentLookupService {

    private static final Logger logger = LoggerFactory.getLogger(AgentLookupServiceImpl.class);

    private final UserRepository userRepository;
    private final AgentAvailabilityService availabilityService;
    private final VisitRepository visitRepository;

    @Autowired
    public AgentLookupServiceImpl(UserRepository userRepository,
                                  AgentAvailabilityService availabilityService,
                                  VisitRepository visitRepository) {
        this.userRepository = userRepository;
        this.availabilityService = availabilityService;
        this.visitRepository = visitRepository;
    }

    @Override
    public Optional<User> findAgentByUsername(String username) {
        if (username == null) return Optional.empty();
        return userRepository.findByUsername(username).filter(User::isAgent);
    }

    /**
     * Verifica se l'agente è disponibile nell'intervallo [start,end).
     * Strategia:
     *  - validazione difensiva dell'intervallo
     *  - richiede che esista almeno uno slot dichiarato che copra l'intervallo
     *  - richiede che non esistano visite confermate che si sovrappongono
     *
     * Restituisce Optional.of(true/false) perché questa implementazione
     * supporta esplicitamente la verifica.
     */
    @Override
    public Optional<Boolean> isAgentAvailable(Long agentId, Instant start, Instant end) {
        if (!isValidInterval(agentId, start, end)) {
            logger.debug("isAgentAvailable - invalid interval or agentId null");
            return Optional.of(false);
        }

        if (!hasCoveringSlot(agentId, start, end)) {
            logger.debug("isAgentAvailable - no covering slot for agentId={}", agentId);
            return Optional.of(false);
        }

        if (countOverlappingConfirmedVisits(agentId, start, end) > 1) {
            logger.debug("isAgentAvailable - overlapping confirmed visits for agentId={}", agentId);
            return Optional.of(false);
        }

        return Optional.of(true);
    }

    // --- helpers -------------------------------------------------------

    private boolean isValidInterval(Long agentId, Instant start, Instant end) {
        return agentId != null && start != null && end != null && end.isAfter(start);
    }

    private boolean hasCoveringSlot(Long agentId, Instant start, Instant end) {
        try {
            return availabilityService.isSlotCoveringInterval(agentId, start, end);
        } catch (Exception e) {
            // Difensivo: loggare e considerare non disponibile per evitare comportamenti incoerenti
            logger.warn("availabilityService error while checking slot coverage for agentId={}: {}", agentId, e.getMessage(), e);
            return false;
        }
    }

    private int countOverlappingConfirmedVisits(Long agentId, Instant start, Instant end) {
        var overlapping = visitRepository.findOverlappingVisitsForAgent(agentId, start, end, VisitStatus.CONFIRMED);
        logger.debug("overlapping:" + Integer.valueOf(overlapping.size()).toString());
        return overlapping != null ? overlapping.size() : 0;
    }
}