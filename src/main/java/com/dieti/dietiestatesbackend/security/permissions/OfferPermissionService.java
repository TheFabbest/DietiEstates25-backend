package com.dieti.dietiestatesbackend.security.permissions;

import com.dieti.dietiestatesbackend.security.AppPrincipal;

/**
 * Interfaccia per le autorizzazioni relative alle offerte.
 */
public interface OfferPermissionService {

    /**
     * Verifica se il principal è l'agente proprietario dell'offerta (attraverso la property associata).
     */
    boolean isAgentOfOffer(AppPrincipal principal, Long offerId);
}