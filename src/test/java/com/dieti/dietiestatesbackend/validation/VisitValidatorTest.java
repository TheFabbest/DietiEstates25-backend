package com.dieti.dietiestatesbackend.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dieti.dietiestatesbackend.entities.ResidentialProperty;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.entities.Visit;
import com.dieti.dietiestatesbackend.exception.InvalidPayloadException;
import com.dieti.dietiestatesbackend.exception.OverbookingException;
import com.dieti.dietiestatesbackend.repositories.VisitRepository;
import com.dieti.dietiestatesbackend.service.lookup.AgentLookupService;

@ExtendWith(MockitoExtension.class)
public class VisitValidatorTest {

    @Mock
    VisitRepository visitRepository;

    @Mock
    AgentLookupService agentLookupService;

    VisitValidator validator;

    @BeforeEach
    void setUp() {
        validator = new VisitValidator(visitRepository, agentLookupService, "Europe/Rome", 3, 2);
    }

    @Test
    void validateBusinessRules_validDates_shouldNotThrow() {
        Instant start = Instant.now().plus(2, ChronoUnit.DAYS);
        Instant end = start.plus(1, ChronoUnit.HOURS);

        assertDoesNotThrow(() -> validator.validateBusinessRules(start, end));
    }

    @Test
    void validateBusinessRules_pastDate_shouldThrowInvalidPayload() {
        Instant start = Instant.now().minus(2, ChronoUnit.DAYS);
        Instant end = start.plus(1, ChronoUnit.HOURS);

        assertThrows(InvalidPayloadException.class, () -> validator.validateBusinessRules(start, end));
    }

    @Test
    void ensureUserHasNoOverlap_whenRepositoryReturnsOverlap_shouldThrow() {
        Instant start = Instant.now().plus(2, ChronoUnit.DAYS);
        Instant end = start.plus(1, ChronoUnit.HOURS);

        when(visitRepository.findOverlappingVisitsForUserWithLock(any(), any(), any(), any()))
                .thenReturn(List.of(new Visit()));

        assertThrows(InvalidPayloadException.class, () -> validator.ensureUserHasNoOverlap(1L, start, end));
    }

    @Test
    void ensureAgentAvailable_legacyNullService_shouldAssumeAvailable() {
        VisitValidator legacy = new VisitValidator(visitRepository, null, "Europe/Rome", 3, 2);
        Instant start = Instant.now().plus(2, ChronoUnit.DAYS);
        Instant end = start.plus(1, ChronoUnit.HOURS);

        when(visitRepository.findOverlappingVisitsForAgentWithLock(any(), any(), any(), any()))
                .thenReturn(List.of());

        assertDoesNotThrow(() -> legacy.ensureAgentAvailable(5L, start, end));
    }

    @Test
    void ensureAgentAvailable_lookupReturnsFalse_shouldThrowInvalidPayload() {
        Instant start = Instant.now().plus(2, ChronoUnit.DAYS);
        Instant end = start.plus(1, ChronoUnit.HOURS);

        when(visitRepository.findOverlappingVisitsForAgentWithLock(any(), any(), any(), any()))
                .thenReturn(List.of());
        when(agentLookupService.isAgentAvailable(any(), any(), any())).thenReturn(Optional.of(false));

        assertThrows(InvalidPayloadException.class, () -> validator.ensureAgentAvailable(7L, start, end));
    }

    @Test
    void ensureOverbookingRules_samePropertyExceeded_shouldThrowOverbookingException() {
        Instant start = Instant.now().plus(2, ChronoUnit.DAYS);
        Instant end = start.plus(1, ChronoUnit.HOURS);

        when(visitRepository.countConfirmedVisitsForPropertyWithLock(any(), any(), any(), any()))
                .thenReturn(3L);

        Visit v = new Visit();
        ResidentialProperty prop = new ResidentialProperty();
        prop.setId(11L);
        User agent = new User();
        agent.setId(21L);
        v.setProperty(prop);
        v.setAgent(agent);
        v.setStartTime(start);
        v.setEndTime(end);

        assertThrows(OverbookingException.class, () -> validator.ensureOverbookingRules(v));
    }

    @Test
    void ensureOverbookingRules_distinctPropertiesExceeded_shouldThrowOverbookingException() {
        Instant start = Instant.now().plus(2, ChronoUnit.DAYS);
        Instant end = start.plus(1, ChronoUnit.HOURS);

        when(visitRepository.countConfirmedVisitsForPropertyWithLock(any(), any(), any(), any()))
                .thenReturn(0L);
        when(visitRepository.countDistinctConfirmedPropertiesForAgentWithLock(any(), any(), any(), any(), any()))
                .thenReturn(2L);

        Visit v = new Visit();
        ResidentialProperty prop = new ResidentialProperty();
        prop.setId(12L);
        User agent = new User();
        agent.setId(22L);
        v.setProperty(prop);
        v.setAgent(agent);
        v.setStartTime(start);
        v.setEndTime(end);

        assertThrows(OverbookingException.class, () -> validator.ensureOverbookingRules(v));
    }
}