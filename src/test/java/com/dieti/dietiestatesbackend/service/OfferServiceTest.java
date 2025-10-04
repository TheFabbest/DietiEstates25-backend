package com.dieti.dietiestatesbackend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.dieti.dietiestatesbackend.entities.Offer;
import com.dieti.dietiestatesbackend.repositories.OfferRepository;
import com.dieti.dietiestatesbackend.dto.response.OfferResponseDTO;
import com.dieti.dietiestatesbackend.enums.OfferStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class OfferServiceTest {
    
    @Mock
    private OfferRepository offerRepository;

    @InjectMocks
    private OfferService offerService;

    @Test
    void testGetOffer_Success() {
        // Given
        Long offerId = 1L;
        Offer offer = new Offer();
        offer.setId(offerId);
        offer.setPrice(new BigDecimal("150000.00"));
        offer.setStatus(OfferStatus.PENDING);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        // When
        Offer result = offerService.getOffer(offerId);

        // Then
        assertNotNull(result);
        assertEquals(offerId, result.getId());
        assertEquals(new BigDecimal("150000.00"), result.getPrice());
        assertEquals(OfferStatus.PENDING, result.getStatus());
        
        verify(offerRepository, times(1)).findById(offerId);
    }

    @Test
    void testGetOffer_NotFound() {
        // Given
        Long offerId = 999L;
        when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(com.dieti.dietiestatesbackend.exception.EntityNotFoundException.class, 
            () -> offerService.getOffer(offerId));
        
        verify(offerRepository, times(1)).findById(offerId);
    }

    @Test
    void testGetAgentOffers_Success() {
        // Given
        Long agentId = 1L;
        Pageable pageable = PageRequest.of(0, 5);
        
        Offer offer1 = new Offer();
        offer1.setId(1L);
        offer1.setPrice(new BigDecimal("150000.00"));
        offer1.setStatus(OfferStatus.PENDING);

        Offer offer2 = new Offer();
        offer2.setId(2L);
        offer2.setPrice(new BigDecimal("200000.00"));
        offer2.setStatus(OfferStatus.ACCEPTED);

        List<Offer> offers = Arrays.asList(offer1, offer2);
        Page<Offer> offerPage = new PageImpl<>(offers, pageable, offers.size());

        when(offerRepository.getAgentOffers(agentId, pageable)).thenReturn(offerPage);

        // When
        Page<Offer> result = offerService.getAgentOffers(agentId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(offer1.getId(), result.getContent().get(0).getId());
        assertEquals(offer2.getId(), result.getContent().get(1).getId());
        
        verify(offerRepository, times(1)).getAgentOffers(agentId, pageable);
    }

    @Test
    void testGetAgentOffers_Empty() {
        // Given
        Long agentId = 1L;
        Pageable pageable = PageRequest.of(0, 5);
        Page<Offer> emptyPage = Page.empty(pageable);

        when(offerRepository.getAgentOffers(agentId, pageable)).thenReturn(emptyPage);

        // When
        Page<Offer> result = offerService.getAgentOffers(agentId, pageable);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getContent().size());
        
        verify(offerRepository, times(1)).getAgentOffers(agentId, pageable);
    }

    @Test
    void testMapToResponseDTO_Success() {
        // Given
        Offer offer = new Offer();
        offer.setId(1L);
        offer.setPrice(new BigDecimal("150000.00"));
        offer.setStatus(OfferStatus.PENDING);
        offer.setCreatedAt(LocalDateTime.now());

        // When
        OfferResponseDTO result = offerService.mapToResponseDTO(offer);

        // Then
        assertNotNull(result);
        assertEquals(offer.getId(), result.getId());
        assertEquals(offer.getPrice(), result.getPrice());
        assertEquals(offer.getStatus(), result.getStatus());
        assertEquals(offer.getCreatedAt(), result.getCreatedAt());
        assertNull(result.getProperty()); // Property non mappato nel metodo base
        assertNull(result.getUser()); // User non mappato nel metodo base
    }

    @Test
    void testMapToResponseDTO_NullOffer() {
        // Given
        Offer offer = null;

        // When & Then
        assertThrows(NullPointerException.class, 
            () -> offerService.mapToResponseDTO(offer));
    }
}