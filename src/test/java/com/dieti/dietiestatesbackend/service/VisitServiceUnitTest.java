package com.dieti.dietiestatesbackend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dieti.dietiestatesbackend.dto.request.VisitCreationRequestDTO;
import com.dieti.dietiestatesbackend.dto.response.AgentVisitDTO;
import com.dieti.dietiestatesbackend.dto.response.AddressResponseDTO; // Aggiunto import
import com.dieti.dietiestatesbackend.entities.ResidentialProperty;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.entities.Visit;
import com.dieti.dietiestatesbackend.enums.VisitStatus;
import com.dieti.dietiestatesbackend.exception.InvalidPayloadException;
import com.dieti.dietiestatesbackend.repositories.PropertyRepository;
import com.dieti.dietiestatesbackend.repositories.UserRepository;
import com.dieti.dietiestatesbackend.repositories.VisitRepository;
import com.dieti.dietiestatesbackend.validation.VisitValidator;

@ExtendWith(MockitoExtension.class)
class VisitServiceUnitTest {

    @Mock
    VisitRepository visitRepository;

    @Mock
    PropertyRepository propertyRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    VisitValidator visitValidator;

    VisitService visitService;

    @BeforeEach
    void setUp() {
        visitService = new VisitService(visitRepository, propertyRepository, userRepository, visitValidator);
    }

    @Test
    void createVisit_valid_savesAndReturnsAgentVisitDTOWithPendingStatus() {
        Instant start = Instant.now().plus(2, ChronoUnit.DAYS);
        Instant end = start.plus(1, ChronoUnit.HOURS);

        VisitCreationRequestDTO req = new VisitCreationRequestDTO();
        req.setPropertyId(1L);
        req.setAgentId(2L);
        req.setStartTime(start);
        req.setEndTime(end);

        ResidentialProperty property = new ResidentialProperty();
        property.setId(1L);

        User agent = new User();
        agent.setId(2L);
        agent.setAgent(true);

        User user = new User();
        user.setId(30L);

        Visit saved = new Visit();
        saved.setId(5L);
        saved.setProperty(property);
        saved.setAgent(agent);
        saved.setUser(user);
        saved.setStartTime(start);
        saved.setEndTime(end);
        saved.setStatus(VisitStatus.PENDING);

        when(propertyRepository.findDetailedById(1L)).thenReturn(Optional.of(property));
        when(userRepository.findById(2L)).thenReturn(Optional.of(agent));
        when(userRepository.findById(30L)).thenReturn(Optional.of(user));
        when(visitRepository.save(any())).thenReturn(saved);
        when(visitRepository.findAgentVisitById(5L)).thenReturn(Optional.of(new AgentVisitDTO(saved, "RESIDENTIAL", new AddressResponseDTO())));

        var dto = visitService.createVisit(req, 30L);

        assertNotNull(dto);
        assertEquals(VisitStatus.PENDING, dto.getVisit().getStatus());
        verify(visitValidator).validateBusinessRules(start, end);
        verify(visitValidator).ensureUserHasOneOrNoOverlap(30L, start, end);
        verify(visitValidator).ensureAgentAvailable(2L, start, end);
    }

    @Test
    void createVisit_invalidValidator_throwsInvalidPayload() {
        Instant start = Instant.now().minus(2, ChronoUnit.DAYS);
        Instant end = start.plus(1, ChronoUnit.HOURS);

        VisitCreationRequestDTO req = new VisitCreationRequestDTO();
        req.setPropertyId(1L);
        req.setAgentId(2L);
        req.setStartTime(start);
        req.setEndTime(end);

        doThrow(new InvalidPayloadException(java.util.Map.of("time", "invalid"))).when(visitValidator).validateBusinessRules(start, end);

        assertThrows(InvalidPayloadException.class, () -> visitService.createVisit(req, 30L));
        verify(visitRepository, never()).save(any());
    }

    @Test
    void updateVisitStatus_confirmPending_shouldSetConfirmedAndCallValidator() {
        Visit visit = new Visit();
        visit.setId(10L);
        visit.setStatus(VisitStatus.PENDING);

        User agent = new User();
        agent.setId(20L);
        visit.setAgent(agent);

        User user = new User();
        user.setId(30L);
        visit.setUser(user);

        visit.setStartTime(Instant.now().plus(2, ChronoUnit.DAYS));
        visit.setEndTime(visit.getStartTime().plus(1, ChronoUnit.HOURS));

        when(visitRepository.findById(10L)).thenReturn(Optional.of(visit));
        when(visitRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(visitRepository.findAgentVisitById(10L)).thenReturn(Optional.of(new AgentVisitDTO(visit, "COMMERCIAL", new AddressResponseDTO())));

        var result = visitService.updateVisitStatus(10L, VisitStatus.CONFIRMED);

        assertNotNull(result);
        assertEquals(VisitStatus.CONFIRMED, result.getVisit().getStatus());
        verify(visitValidator).ensureOverbookingRules(visit);
    }

    @Test
    void updateVisitStatus_invalidTransition_throwsInvalidPayloadException() {
        Visit visit = new Visit();
        visit.setId(11L);
        visit.setStatus(VisitStatus.PENDING);

        when(visitRepository.findById(11L)).thenReturn(Optional.of(visit));

        assertThrows(InvalidPayloadException.class, () -> visitService.updateVisitStatus(11L, VisitStatus.COMPLETED));
    }
}