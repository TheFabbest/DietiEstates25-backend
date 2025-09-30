package com.dieti.dietiestatesbackend.validation;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dieti.dietiestatesbackend.entities.Visit;
import com.dieti.dietiestatesbackend.enums.VisitStatus;
import com.dieti.dietiestatesbackend.exception.InvalidPayloadException;
import com.dieti.dietiestatesbackend.exception.OverbookingException;
import com.dieti.dietiestatesbackend.repositories.VisitRepository;
import com.dieti.dietiestatesbackend.service.lookup.AgentLookupService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class VisitValidator {

    private static final Logger logger = LoggerFactory.getLogger(VisitValidator.class);

    private static final String FIELD_TIME = "time";
    private static final String FIELD_END_TIME = "endTime";
    private static final String FIELD_START_TIME = "startTime";
    private static final String FIELD_OVERLAP = "overlap";
    private static final String FIELD_AGENT = "agent";
    private static final String MSG_MISSING_TIME = "Start e end devono essere forniti";
    private static final String MSG_END_AFTER_START = "L'orario di fine deve essere successivo all'orario di inizio";
    private static final String MSG_MIN_BOOKING_DATE = "Le visite possono essere prenotate solo a partire dal giorno successivo";
    private static final String MSG_USER_OVERLAP = "L'utente ha già una visita confermata nello stesso intervallo orario";
    private static final String MSG_AGENT_UNAVAILABLE = "L'agente non è disponibile per l'intervallo selezionato";
    private static final String MSG_AGENT_NO_DECLARED_AVAILABILITY = "L'agente non ha dichiarato disponibilità per lo slot richiesto";
    private static final String MSG_AGENT_CHECK_FAILED = "Impossibile verificare la disponibilità dell'agente al momento";

    private final VisitRepository visitRepository;
    private final AgentLookupService agentLookupService;
    private final ZoneId zoneId;
    private final int maxConfirmedSameProperty;
    private final int maxDistinctPropertiesForAgent;

    @Autowired
    public VisitValidator(VisitRepository visitRepository,
                          AgentLookupService agentLookupService,
                          @Value("${app.timezone:Europe/Rome}") String timezone,
                          @Value("${overbooking.max.same-property:3}") int maxConfirmedSameProperty,
                          @Value("${overbooking.max.distinct-properties:2}") int maxDistinctPropertiesForAgent) {
        this.visitRepository = visitRepository;
        this.agentLookupService = agentLookupService;
        this.zoneId = ZoneId.of(timezone);
        this.maxConfirmedSameProperty = maxConfirmedSameProperty;
        this.maxDistinctPropertiesForAgent = maxDistinctPropertiesForAgent;
    }

    public void validateBusinessRules(Instant start, Instant end) {
        requireStartAndEnd(start, end);
        ensureEndAfterStart(start, end);
        ensureBookingDateNotInPast(start);
    }

    private void requireStartAndEnd(Instant start, Instant end) {
        if (start == null || end == null) {
            throw new InvalidPayloadException(Map.of(FIELD_TIME, MSG_MISSING_TIME));
        }
    }

    private void ensureEndAfterStart(Instant start, Instant end) {
        if (!end.isAfter(start)) {
            throw new InvalidPayloadException(Map.of(FIELD_END_TIME, MSG_END_AFTER_START));
        }
    }

    private void ensureBookingDateNotInPast(Instant start) {
        LocalDate visitDate = toLocalDate(start);
        LocalDate minAllowed = LocalDate.now(zoneId).plusDays(1);
        if (visitDate.isBefore(minAllowed)) {
            throw new InvalidPayloadException(Map.of(FIELD_START_TIME, MSG_MIN_BOOKING_DATE));
        }
    }

    private LocalDate toLocalDate(Instant instant) {
        return instant.atZone(zoneId).toLocalDate();
    }

    public void ensureUserHasNoOverlap(Long userId, Instant start, Instant end) {
        List<Visit> overlapping = visitRepository.findOverlappingVisitsForUserWithLock(userId, start, end, VisitStatus.CONFIRMED);
        throwInvalidPayloadIfNotEmpty(overlapping, FIELD_OVERLAP, MSG_USER_OVERLAP);
    }

    public void ensureAgentAvailable(Long agentId, Instant start, Instant end) {
        List<Visit> overlappingForAgent = visitRepository.findOverlappingVisitsForAgentWithLock(agentId, start, end, VisitStatus.CONFIRMED);
        throwInvalidPayloadIfNotEmpty(overlappingForAgent, FIELD_AGENT, MSG_AGENT_UNAVAILABLE);

        if (agentLookupService == null) {
            // legacy behavior: if no external service, assume available
            return;
        }

        try {
            Optional<Boolean> availability = agentLookupService.isAgentAvailable(agentId, start, end);
            if (availability == null) {
                // Defensive: treat null like empty Optional (log and assume available)
                logger.debug("AgentLookupService returned null availability for agentId={}", agentId);
                return;
            }
            if (availability.isPresent()) {
                if (!availability.get()) {
                    throw new InvalidPayloadException(Map.of(FIELD_AGENT, MSG_AGENT_NO_DECLARED_AVAILABILITY));
                }
            } else {
                logger.debug("AgentLookupService did not return availability info for agentId={}", agentId);
                // assume available to preserve backward compatibility
            }
        } catch (Exception e) {
            logger.warn("AgentLookupService error while checking availability for agentId={}: {}", agentId, e.getMessage());
            throw new InvalidPayloadException(Map.of(FIELD_AGENT, MSG_AGENT_CHECK_FAILED));
        }
    }

    /**
     * Verifica le regole di overbooking per una visita.
     * Vincolo 5: Max 3 visite confermate sulla stessa proprietà (gruppo).
     * Vincolo 6: Max 2 visite confermate su proprietà diverse per lo stesso agente.
     *
     * IMPORTANTE: Il conteggio include la visita corrente che si sta per confermare,
     * quindi usiamo >= invece di > per confrontare con i limiti configurati.
     */
    public void ensureOverbookingRules(Visit visit) {
        Instant start = visit.getStartTime();
        Instant end = visit.getEndTime();
        Long propertyId = visit.getProperty().getId();
        Long agentId = visit.getAgent().getId();

        // Vincolo 5: Massimo visite confermate sulla stessa proprietà
        long confirmedOnSameProperty = visitRepository.countConfirmedVisitsForPropertyWithLock(propertyId, start, end, VisitStatus.CONFIRMED);
        if (confirmedOnSameProperty >= maxConfirmedSameProperty) {
            throw new OverbookingException(Map.of("overbooking",
                    "Overbooking sulla stessa proprietà: massimo " + maxConfirmedSameProperty + " visite confermate simultanee. L'agente deve scegliere quali confermare/annullare."));
        }

        // Vincolo 6: Massimo visite confermate su proprietà diverse per l'agente
        long distinctProperties = visitRepository.countDistinctConfirmedPropertiesForAgentWithLock(agentId, propertyId, start, end, VisitStatus.CONFIRMED);
        if (distinctProperties >= maxDistinctPropertiesForAgent) {
            throw new OverbookingException(Map.of("overbooking",
                    "Overbooking agente su proprietà diverse: massimo " + maxDistinctPropertiesForAgent + " visite confermate simultanee su proprietà diverse. L'agente deve risolvere i conflitti."));
        }
    }

    private void throwInvalidPayloadIfNotEmpty(List<?> list, String field, String message) {
        if (list != null && !list.isEmpty()) {
            throw new InvalidPayloadException(Map.of(field, message));
        }
    }
}