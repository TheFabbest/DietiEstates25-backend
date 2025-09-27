package com.dieti.dietiestatesbackend.service;

import com.dieti.dietiestatesbackend.dto.response.AgentVisitDTO;
import com.dieti.dietiestatesbackend.repositories.VisitRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



class VisitServiceTest {

    @Mock
    Page<AgentVisitDTO> mockPage;

    @Test
    void testGetAgentVisits_ReturnsPage() {
        VisitRepository visitRepository = mock(VisitRepository.class);
        VisitService visitService = new VisitService(visitRepository);

        Long agentId = 1L;
        Pageable pageable = mock(Pageable.class);

        when(visitRepository.getAgentVisits(agentId, pageable)).thenReturn(mockPage);

        Page<AgentVisitDTO> result = visitService.getAgentVisits(agentId, pageable);

        assertEquals(mockPage, result);
        verify(visitRepository).getAgentVisits(agentId, pageable);
    }
}