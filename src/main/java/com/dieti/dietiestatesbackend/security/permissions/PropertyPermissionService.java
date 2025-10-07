package com.dieti.dietiestatesbackend.security.permissions;

import com.dieti.dietiestatesbackend.security.AppPrincipal;

/**
 * Interfaccia per le autorizzazioni relative alle Property.
 * I metodi devono interrogare il livello di business (PropertyService) attraverso
 * l'implementazione, mai accedere ai repository direttamente.
 */
public interface PropertyPermissionService {

    boolean canAccessProperty(AppPrincipal principal, Long propertyId);

    boolean canViewPropertyVisits(AppPrincipal principal, Long propertyId);
}