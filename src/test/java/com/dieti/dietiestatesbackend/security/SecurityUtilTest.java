package com.dieti.dietiestatesbackend.security;

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

import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.entities.Visit;
import com.dieti.dietiestatesbackend.enums.VisitStatus;
import com.dieti.dietiestatesbackend.repositories.PropertyRepository;
import com.dieti.dietiestatesbackend.repositories.VisitRepository;
import com.dieti.dietiestatesbackend.repositories.AgentAvailabilityRepository;

@ExtendWith(MockitoExtension.class)
class SecurityUtilTest {

    @Mock
    PropertyRepository propertyRepository;

    @Mock
    VisitRepository visitRepository;

    @Mock
    AgentAvailabilityRepository agentAvailabilityRepository;

    private SecurityUtil securityUtil;

    @BeforeEach
    void setUp() {
        securityUtil = new SecurityUtil(propertyRepository, visitRepository, agentAvailabilityRepository, 24);
    }

    @Test
    void managerCanAlwaysCancel() {
        AppPrincipal principal = mock(AppPrincipal.class);
        when(principal.isManager()).thenReturn(true);

        boolean allowed = securityUtil.canCancelVisit(principal, 1L);

        assertTrue(allowed);
    }

    @Test
    void agentCanCancelOwnVisit() {
        AppPrincipal principal = mock(AppPrincipal.class);
        when(principal.isManager()).thenReturn(false);
        when(principal.getId()).thenReturn(10L);

        Visit visit = new Visit();
        User agent = new User();
        agent.setId(10L);
        visit.setAgent(agent);
        visit.setStatus(VisitStatus.CONFIRMED);
        visit.setStartTime(Instant.now().plus(2, ChronoUnit.DAYS));

        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));

        assertTrue(securityUtil.canCancelVisit(principal, 1L));
    }

    @Test
    void ownerCanCancelPendingVisit() {
        AppPrincipal principal = mock(AppPrincipal.class);
        when(principal.isManager()).thenReturn(false);
        when(principal.getId()).thenReturn(20L);

        Visit visit = new Visit();
        User user = new User();
        user.setId(20L);
        visit.setUser(user);
        visit.setStatus(VisitStatus.PENDING);
        visit.setStartTime(Instant.now().plus(2, ChronoUnit.DAYS));

        when(visitRepository.findById(2L)).thenReturn(Optional.of(visit));

        assertTrue(securityUtil.canCancelVisit(principal, 2L));
    }

    @Test
    void ownerCannotCancelConfirmedWithin24Hours() {
        AppPrincipal principal = mock(AppPrincipal.class);
        when(principal.isManager()).thenReturn(false);
        when(principal.getId()).thenReturn(30L);

        Visit visit = new Visit();
        User user = new User();
        user.setId(30L);
        visit.setUser(user);
        visit.setStatus(VisitStatus.CONFIRMED);
        visit.setStartTime(Instant.now().plus(12, ChronoUnit.HOURS));

        when(visitRepository.findById(3L)).thenReturn(Optional.of(visit));

        assertFalse(securityUtil.canCancelVisit(principal, 3L));
    }

    @Test
    void ownerCanCancelConfirmedMoreThan24Hours() {
        AppPrincipal principal = mock(AppPrincipal.class);
        when(principal.isManager()).thenReturn(false);
        when(principal.getId()).thenReturn(40L);

        Visit visit = new Visit();
        User user = new User();
        user.setId(40L);
        visit.setUser(user);
        visit.setStatus(VisitStatus.CONFIRMED);
        visit.setStartTime(Instant.now().plus(48, ChronoUnit.HOURS));

        when(visitRepository.findById(4L)).thenReturn(Optional.of(visit));

        assertTrue(securityUtil.canCancelVisit(principal, 4L));
    }

    @Test
    void nonAuthorizedCannotCancel() {
        AppPrincipal principal = mock(AppPrincipal.class);
        when(principal.isManager()).thenReturn(false);
        when(principal.getId()).thenReturn(50L);

        Visit visit = new Visit();
        User user = new User();
        user.setId(60L);
        visit.setUser(user);
        User agent = new User();
        agent.setId(70L);
        visit.setAgent(agent);
        visit.setStatus(VisitStatus.CONFIRMED);
        visit.setStartTime(Instant.now().plus(48, ChronoUnit.HOURS));

        when(visitRepository.findById(5L)).thenReturn(Optional.of(visit));

        assertFalse(securityUtil.canCancelVisit(principal, 5L));
    }

}