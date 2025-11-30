package com.dieti.dietiestatesbackend.security.permissions;

import com.dieti.dietiestatesbackend.dto.request.VisitCreationRequestDTO;
import com.dieti.dietiestatesbackend.enums.VisitStatus;
import com.dieti.dietiestatesbackend.security.AppPrincipal;

/**
 * Facade centrale per la gestione dei permessi.
 * Punto di ingresso unico per tutte le verifiche di autorizzazione basate su logica di business.
 */
public interface PermissionFacade {

    // #region Property Permissions
    boolean canAccessProperty(AppPrincipal principal, Long propertyId);
    boolean canViewPropertyVisits(AppPrincipal principal, Long propertyId);
    boolean isAgentOfProperty(AppPrincipal principal, Long propertyId);
    // #endregion

    // #region Agent Permissions
    boolean isAgentOrManager(AppPrincipal principal, Long agentId);
    boolean canViewAgentRelatedEntities(AppPrincipal principal, Long agentId);
    boolean canManageAgentAvailability(AppPrincipal principal, Long availabilityId);
    // #endregion

    // #region Visit Permissions
    boolean canCreateVisit(AppPrincipal principal, VisitCreationRequestDTO request);
    boolean canUpdateVisitStatus(AppPrincipal principal, Long visitId, VisitStatus newStatus);
    boolean canCancelVisit(AppPrincipal principal, Long visitId);
    // #endregion

    // #region Offer Permissions
    boolean isAgentOfOffer(AppPrincipal principal, Long offerId);
    // #endregion

    // #region Contract Permissions
    boolean canAccessContract(AppPrincipal principal, Long contractId);
    // #endregion
}