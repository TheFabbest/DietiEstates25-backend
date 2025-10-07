package com.dieti.dietiestatesbackend.security.permissions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dieti.dietiestatesbackend.entities.Offer;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.security.AppPrincipal;
import com.dieti.dietiestatesbackend.security.permissions.impl.OfferPermissionServiceImpl;
import com.dieti.dietiestatesbackend.service.OfferService;

@ExtendWith(MockitoExtension.class)
class OfferPermissionServiceImplTest {

    @Mock
    private OfferService offerService;

    @InjectMocks
    private OfferPermissionServiceImpl offerPermissionService;

    @Test
    void isAgentOfOffer_shouldReturnTrue_whenPrincipalIsManager() {
        AppPrincipal principal = mock(AppPrincipal.class);
        when(principal.isManager()).thenReturn(true);

        assertTrue(offerPermissionService.isAgentOfOffer(principal, 100L),
                "Manager principal must be allowed without fetching the offer");
        verify(offerService, never()).getOffer(anyLong());
    }

    @Test
    void isAgentOfOffer_shouldReturnTrue_whenPrincipalIsAgentAndOwnsOffer() {
        AppPrincipal principal = mock(AppPrincipal.class);
        when(principal.isManager()).thenReturn(false);
        when(principal.getId()).thenReturn(1L);

        User agent = mock(User.class);
        when(agent.getId()).thenReturn(1L);

        Property property = mock(Property.class);
        when(property.getAgent()).thenReturn(agent);

        Offer offer = mock(Offer.class);
        when(offer.getId()).thenReturn(100L);
        when(offer.getProperty()).thenReturn(property);

        when(offerService.getOffer(offer.getId())).thenReturn(offer);

        assertTrue(offerPermissionService.isAgentOfOffer(principal, offer.getId()),
                "Agent principal who owns the offer's property should be allowed");
        verify(offerService).getOffer(offer.getId());
    }

    @Test
    void isAgentOfOffer_shouldReturnFalse_whenPrincipalIsAgentAndDoesNotOwnOffer() {
        AppPrincipal principal = mock(AppPrincipal.class);
        when(principal.isManager()).thenReturn(false);
        when(principal.getId()).thenReturn(2L);

        User agent = mock(User.class);
        when(agent.getId()).thenReturn(1L);

        Property property = mock(Property.class);
        when(property.getAgent()).thenReturn(agent);

        Offer offer = mock(Offer.class);
        when(offer.getId()).thenReturn(100L);
        when(offer.getProperty()).thenReturn(property);

        when(offerService.getOffer(offer.getId())).thenReturn(offer);

        assertFalse(offerPermissionService.isAgentOfOffer(principal, offer.getId()),
                "Agent principal who does not own the offer's property must not be allowed");
        verify(offerService).getOffer(offer.getId());
    }

    @Test
    void isAgentOfOffer_shouldReturnFalse_whenOfferNotFound() {
        AppPrincipal principal = mock(AppPrincipal.class);
        when(principal.isManager()).thenReturn(false);
        when(principal.getId()).thenReturn(1L);

        Long offerId = 100L;
        when(offerService.getOffer(offerId)).thenReturn(null);

        assertFalse(offerPermissionService.isAgentOfOffer(principal, offerId),
                "If the offer cannot be fetched the permission check must return false");
        verify(offerService).getOffer(offerId);
    }

    @Test
    void isAgentOfOffer_shouldReturnFalse_whenPrincipalIsNotAgentOrManager() {
        AppPrincipal principal = mock(AppPrincipal.class);
        when(principal.isManager()).thenReturn(false);
        when(principal.getId()).thenReturn(2L);

        User agent = mock(User.class);
        when(agent.getId()).thenReturn(1L);

        Property property = mock(Property.class);
        when(property.getAgent()).thenReturn(agent);

        Offer offer = mock(Offer.class);
        when(offer.getId()).thenReturn(100L);
        when(offer.getProperty()).thenReturn(property);

        when(offerService.getOffer(offer.getId())).thenReturn(offer);

        assertFalse(offerPermissionService.isAgentOfOffer(principal, offer.getId()),
                "Principal who is neither manager nor owner agent must not be allowed");
        verify(offerService).getOffer(offer.getId());
    }
}