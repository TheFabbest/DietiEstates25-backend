package com.dieti.dietiestatesbackend.controller;

import com.dieti.dietiestatesbackend.dto.response.AgentVisitDTO;
import com.dieti.dietiestatesbackend.security.AuthenticatedUser;
import com.dieti.dietiestatesbackend.security.SecurityUtil;
import com.dieti.dietiestatesbackend.service.VisitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import com.dieti.dietiestatesbackend.entities.Address;
import com.dieti.dietiestatesbackend.entities.Visit;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class VisitControllerTest {

    @Mock
    private VisitService visitService;

    @Mock
    private SecurityUtil securityUtil;
    
    @InjectMocks  // This annotation was missing!
    private VisitController visitController;

    @BeforeEach
    void setUp() {
        // Mock authentication for @PreAuthorize
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(1L, "test@example.com", true, List.of(new SimpleGrantedAuthority("ROLE_AGENT")));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(authenticatedUser, null, authenticatedUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void getAgentVisits_shouldReturnVisits_whenAuthorized() throws Exception {
        // Given
        Long agentId = 1L;
        Visit visit = new Visit();
        visit.setId(1L);
        Address address = new Address();
        AgentVisitDTO agentVisitDTO = new AgentVisitDTO(visit, "RESIDENTIAL", address);
        Page<AgentVisitDTO> visitsPage = new PageImpl<>(Collections.singletonList(agentVisitDTO));

        when(visitService.getAgentVisits(anyLong(), any(Pageable.class))).thenReturn(visitsPage);

        // When & Then
        ResponseEntity<Page<AgentVisitDTO>> response = visitController.getAgentVisits(agentId, Pageable.ofSize(10).withPage(1));

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
    }
}