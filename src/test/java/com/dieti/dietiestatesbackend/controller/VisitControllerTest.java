package com.dieti.dietiestatesbackend.controller;

import com.dieti.dietiestatesbackend.dto.response.AgentVisitDTO;
import com.dieti.dietiestatesbackend.dto.response.AddressResponseDTO;
import com.dieti.dietiestatesbackend.entities.Coordinates;
import com.dieti.dietiestatesbackend.entities.ResidentialProperty;
import com.dieti.dietiestatesbackend.security.AppPrincipal;
import com.dieti.dietiestatesbackend.security.SecurityUtil;
import com.dieti.dietiestatesbackend.service.VisitService;
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
import com.dieti.dietiestatesbackend.entities.Address;
import com.dieti.dietiestatesbackend.entities.Visit;
import java.math.BigDecimal;

import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class VisitControllerTest {

    @Mock
    private VisitService visitService;

    @Mock
    private SecurityUtil securityUtil;
    
    @InjectMocks
    private VisitController visitController;

    @Test
    void getAgentVisits_shouldReturnVisits_whenAuthorized() throws Exception {
        // Given
        Visit visit = new Visit();
        visit.setId(1L);
        Coordinates coordinates = new Coordinates(BigDecimal.valueOf(10.0), BigDecimal.valueOf(20.0));
        Address address = new Address("IT", "Rome", "Rome", "Via Roma", "1", "A", coordinates);
        address.setId(1L);
        ResidentialProperty property = new ResidentialProperty();
        property.setId(1L);
        property.setAddress(address);
        visit.setProperty(property);
        AppPrincipal principal = mock(AppPrincipal.class);
        when(principal.getId()).thenReturn(1L);

        AddressResponseDTO addressResponseDTO = new AddressResponseDTO(
                address.getId(),
                address.getCountry(),
                address.getProvince(),
                address.getCity(),
                address.getStreet(),
                address.getStreetNumber(),
                address.getBuilding(),
                address.getCoordinates().getLatitude(),
                address.getCoordinates().getLongitude()
        );
        AgentVisitDTO agentVisitDTO = new AgentVisitDTO(visit, "RESIDENTIAL", addressResponseDTO);
        Page<AgentVisitDTO> visitsPage = new PageImpl<>(Collections.singletonList(agentVisitDTO));

        when(visitService.getAgentVisits(anyLong(), any(Pageable.class))).thenReturn(visitsPage);

        // When & Then
        ResponseEntity<Page<AgentVisitDTO>> response = visitController.getAgentVisits(principal, Pageable.ofSize(10).withPage(1));

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
    }
}