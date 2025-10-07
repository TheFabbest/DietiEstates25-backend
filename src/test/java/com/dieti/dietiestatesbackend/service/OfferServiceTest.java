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
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.User;
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

        // set related user
        User user = new User();
        user.setId(5L);
        user.setFirstName("John");
        user.setLastName("Doe");
        offer.setUser(user);

        // set related property (mocked to avoid abstract instantiation)
        Property property = mock(Property.class);
        when(property.getId()).thenReturn(3L);
        offer.setProperty(property);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        // When
        Offer result = offerService.getOffer(offerId);

        // Then
        assertNotNull(result);
        assertEquals(offerId, result.getId());
        assertEquals(new BigDecimal("150000.00"), result.getPrice());
        assertEquals(OfferStatus.PENDING, result.getStatus());

        // Relationship assertions
        assertNotNull(result.getUser(), "User relation should be present on retrieved Offer");
        assertEquals(user.getId(), result.getUser().getId(), "User id must match mock");

        assertNotNull(result.getProperty(), "Property relation should be present on retrieved Offer");
        assertEquals(property.getId(), result.getProperty().getId(), "Property id must match mock");
        
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

        User user1 = new User();
        user1.setId(11L);
        user1.setFirstName("Alice");
        offer1.setUser(user1);

        Property property1 = mock(Property.class);
        when(property1.getId()).thenReturn(101L);
        offer1.setProperty(property1);

        Offer offer2 = new Offer();
        offer2.setId(2L);
        offer2.setPrice(new BigDecimal("200000.00"));
        offer2.setStatus(OfferStatus.ACCEPTED);

        User user2 = new User();
        user2.setId(12L);
        user2.setFirstName("Bob");
        offer2.setUser(user2);

        Property property2 = mock(Property.class);
        when(property2.getId()).thenReturn(102L);
        offer2.setProperty(property2);

        List<Offer> offers = Arrays.asList(offer1, offer2);
        Page<Offer> offerPage = new PageImpl<>(offers, pageable, offers.size());

        when(offerRepository.getAgentOffers(agentId, pageable)).thenReturn(offerPage);

        // When
        Page<Offer> result = offerService.getAgentOffers(agentId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());

        Offer r1 = result.getContent().get(0);
        Offer r2 = result.getContent().get(1);

        assertEquals(offer1.getId(), r1.getId());
        assertNotNull(r1.getUser(), "User relation for offer1 should be present");
        assertEquals(user1.getId(), r1.getUser().getId(), "User id for offer1 must match mock");
        assertNotNull(r1.getProperty(), "Property relation for offer1 should be present");
        assertEquals(property1.getId(), r1.getProperty().getId(), "Property id for offer1 must match mock");

        assertEquals(offer2.getId(), r2.getId());
        assertNotNull(r2.getUser(), "User relation for offer2 should be present");
        assertEquals(user2.getId(), r2.getUser().getId(), "User id for offer2 must match mock");
        assertNotNull(r2.getProperty(), "Property relation for offer2 should be present");
        assertEquals(property2.getId(), r2.getProperty().getId(), "Property id for offer2 must match mock");
        
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