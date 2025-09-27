package com.dieti.dietiestatesbackend.controller;

import com.dieti.dietiestatesbackend.dto.response.OfferResponseDTO;
import com.dieti.dietiestatesbackend.entities.Offer;
import com.dieti.dietiestatesbackend.security.SecurityUtil;
import com.dieti.dietiestatesbackend.service.OfferService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferControllerTest {

    @InjectMocks
    private OfferController offerController;

    @Mock
    private OfferService offerService;

    @Mock
    private SecurityUtil securityUtil;

    @Test
    void getAgentOffers_shouldReturnOffers_whenServiceReturnsData() {
        // Given
        Long agentId = 1L;
        Offer offer = new Offer();
        offer.setId(1L);
        Page<Offer> offersPage = new PageImpl<>(Collections.singletonList(offer));
        
        OfferResponseDTO offerResponseDTO = new OfferResponseDTO();
        offerResponseDTO.setId(1L);

        when(offerService.getAgentOffers(anyLong(), any(Pageable.class))).thenReturn(offersPage);
        when(offerService.mapToResponseDTO(any(Offer.class))).thenReturn(offerResponseDTO);

        // When
        ResponseEntity<Page<OfferResponseDTO>> response = offerController.getAgentOffers(agentId, PageRequest.of(0, 1));
        
        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(1L, response.getBody().getContent().get(0).getId());
        
        // Verify service calls
        verify(offerService).getAgentOffers(eq(agentId), any(Pageable.class));
        verify(offerService).mapToResponseDTO(offer);
    }

    @Test
    void getAgentOffers_shouldReturnEmptyPage_whenNoOffersFound() {
        // Given
        Long agentId = 1L;
        Page<Offer> emptyPage = new PageImpl<>(Collections.emptyList());
        
        when(offerService.getAgentOffers(anyLong(), any(Pageable.class))).thenReturn(emptyPage);

        // When
        ResponseEntity<Page<OfferResponseDTO>> response = offerController.getAgentOffers(agentId, PageRequest.of(0, 1));
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getContent().isEmpty());
        assertEquals(0, response.getBody().getTotalElements());
        
        verify(offerService).getAgentOffers(eq(agentId), any(Pageable.class));
        verify(offerService, never()).mapToResponseDTO(any(Offer.class));
    }

    @Test
    void getAgentOffers_shouldReturnMultipleOffers_whenMultipleOffersExist() {
        // Given
        Long agentId = 1L;
        Offer offer1 = new Offer();
        offer1.setId(1L);
        Offer offer2 = new Offer();
        offer2.setId(2L);
        
        Page<Offer> offersPage = new PageImpl<>(List.of(offer1, offer2));
        
        OfferResponseDTO dto1 = new OfferResponseDTO();
        dto1.setId(1L);
        OfferResponseDTO dto2 = new OfferResponseDTO();
        dto2.setId(2L);

        when(offerService.getAgentOffers(anyLong(), any(Pageable.class))).thenReturn(offersPage);
        when(offerService.mapToResponseDTO(offer1)).thenReturn(dto1);
        when(offerService.mapToResponseDTO(offer2)).thenReturn(dto2);

        // When
        ResponseEntity<Page<OfferResponseDTO>> response = offerController.getAgentOffers(agentId, PageRequest.of(0, 10));
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getContent().size());
        assertEquals(2, response.getBody().getTotalElements());
        
        List<Long> responseIds = response.getBody().getContent().stream()
                .map(OfferResponseDTO::getId)
                .toList();
        assertTrue(responseIds.contains(1L));
        assertTrue(responseIds.contains(2L));
        
        verify(offerService).getAgentOffers(eq(agentId), any(Pageable.class));
        verify(offerService).mapToResponseDTO(offer1);
        verify(offerService).mapToResponseDTO(offer2);
    }

    @Test
    void getAgentOffers_shouldHandlePagination_correctly() {
        // Given
        Long agentId = 1L;
        Pageable pageable = PageRequest.of(1, 5); // Second page, 5 items per page
        
        Offer offer = new Offer();
        offer.setId(6L); // Item that would appear on second page
        Page<Offer> offersPage = new PageImpl<>(Collections.singletonList(offer), pageable, 10); // Total 10 items
        
        OfferResponseDTO dto = new OfferResponseDTO();
        dto.setId(6L);

        when(offerService.getAgentOffers(agentId, pageable)).thenReturn(offersPage);
        when(offerService.mapToResponseDTO(offer)).thenReturn(dto);

        // When
        ResponseEntity<Page<OfferResponseDTO>> response = offerController.getAgentOffers(agentId, pageable);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(10, response.getBody().getTotalElements());
        assertEquals(2, response.getBody().getTotalPages());
        assertEquals(1, response.getBody().getNumber());
        assertEquals(6L, response.getBody().getContent().get(0).getId());
        
        verify(offerService).getAgentOffers(agentId, pageable);
    }
}