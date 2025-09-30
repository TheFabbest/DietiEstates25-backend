package com.dieti.dietiestatesbackend.service.impl;

import com.dieti.dietiestatesbackend.entities.AgentAvailability;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.enums.VisitStatus;
import com.dieti.dietiestatesbackend.exception.AgentAvailabilityConflictException;
import com.dieti.dietiestatesbackend.exception.AgentAvailabilityNotFoundException;
import com.dieti.dietiestatesbackend.repositories.AgentAvailabilityRepository;
import com.dieti.dietiestatesbackend.repositories.UserRepository;
import com.dieti.dietiestatesbackend.repositories.VisitRepository;
import com.dieti.dietiestatesbackend.service.AgentAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementazione pulita e leggibile di AgentAvailabilityService.
 * Regole principali:
 *  - orari trattati come Instant (UTC)
 *  - validazioni centralizzate e helper privati per chiarezza
 *  - controlli di conflitto con slot dichiarati e visite confermate
 */
@Service
public class AgentAvailabilityServiceImpl implements AgentAvailabilityService {

    private final AgentAvailabilityRepository availabilityRepository;
    private final VisitRepository visitRepository;
    private final UserRepository userRepository;

    @Autowired
    public AgentAvailabilityServiceImpl(AgentAvailabilityRepository availabilityRepository,
                                        VisitRepository visitRepository,
                                        UserRepository userRepository) {
        this.availabilityRepository = availabilityRepository;
        this.visitRepository = visitRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public AgentAvailability create(AgentAvailability availability) {
        validatePayload(availability);

        Long agentId = availability.getAgent().getId();
        Instant start = availability.getStartTime();
        Instant end = availability.getEndTime();

        checkDeclaredOverlap(agentId, start, end, availability.getId());
        checkConfirmedVisitOverlap(agentId, start, end);

        User agent = userRepository.findById(agentId)
                .filter(User::isAgent)
                .orElseThrow(() -> new IllegalArgumentException("Agent not found or user is not an agent"));
        availability.setAgent(agent);

        return availabilityRepository.save(availability);
    }

    @Override
    public List<AgentAvailability> getAvailabilitiesForAgent(Long agentId) {
        if (agentId == null) return List.of();
        return availabilityRepository.findByAgentId(agentId);
    }

    @Override
    public Optional<AgentAvailability> findById(Long id) {
        return availabilityRepository.findById(id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!availabilityRepository.existsById(id)) {
            throw new AgentAvailabilityNotFoundException(id);
        }
        availabilityRepository.deleteById(id);
    }

    @Override
    public boolean isSlotCoveringInterval(Long agentId, Instant start, Instant end) {
        if (agentId == null || start == null || end == null) return false;
        var covering = availabilityRepository.findCoveringIntervalForAgent(agentId, start, end);
        return covering != null && !covering.isEmpty();
    }

    // ----- Helpers -----------------------------------------------------

    private void validatePayload(AgentAvailability a) {
        if (a == null) throw new IllegalArgumentException("availability must not be null");
        if (a.getAgent() == null || a.getAgent().getId() == null) throw new IllegalArgumentException("agent must be provided");
        if (a.getStartTime() == null || a.getEndTime() == null) throw new IllegalArgumentException("start and end must be provided");
        if (!a.getEndTime().isAfter(a.getStartTime())) throw new IllegalArgumentException("end must be after start");
    }

    private void checkDeclaredOverlap(Long agentId, Instant start, Instant end, Long currentAvailabilityId) {
        var overlapping = availabilityRepository.findOverlappingForAgent(agentId, start, end);
        if (overlapping == null || overlapping.isEmpty()) return;

        boolean conflict = overlapping.stream()
                .anyMatch(av -> currentAvailabilityId == null || !av.getId().equals(currentAvailabilityId));

        if (conflict) {
            throw new AgentAvailabilityConflictException(
                    Map.of("availability", "Lo slot si sovrappone con altri slot dichiarati dall'agente"));
        }
    }

    private void checkConfirmedVisitOverlap(Long agentId, Instant start, Instant end) {
        var overlappingVisits = visitRepository.findOverlappingVisitsForAgent(agentId, start, end, VisitStatus.CONFIRMED);
        if (overlappingVisits != null && !overlappingVisits.isEmpty()) {
            throw new AgentAvailabilityConflictException(
                    Map.of("availability", "Lo slot è in conflitto con visite già confermate per l'agente"));
        }
    }
}