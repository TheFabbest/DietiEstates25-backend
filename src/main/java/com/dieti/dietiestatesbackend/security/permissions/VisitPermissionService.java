package com.dieti.dietiestatesbackend.security.permissions;

import com.dieti.dietiestatesbackend.dto.request.VisitCreationRequestDTO;
import com.dieti.dietiestatesbackend.enums.VisitStatus;
import com.dieti.dietiestatesbackend.security.AppPrincipal;

/**
 * Interfaccia per le autorizzazioni relative alle visite.
 * Le implementazioni devono usare il service layer (VisitService, AgentAvailabilityService, ecc.)
 * per recuperare i dati necessari e non accedere direttamente ai repository.
 */
public interface VisitPermissionService {

    boolean canCreateVisit(AppPrincipal principal, VisitCreationRequestDTO request);

    boolean canUpdateVisitStatus(AppPrincipal principal, Long visitId, VisitStatus newStatus);

    boolean canCancelVisit(AppPrincipal principal, Long visitId);
}