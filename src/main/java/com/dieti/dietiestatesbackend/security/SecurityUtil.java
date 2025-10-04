package com.dieti.dietiestatesbackend.security;
 
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
 
import com.dieti.dietiestatesbackend.repositories.PropertyRepository;
import com.dieti.dietiestatesbackend.repositories.VisitRepository;
import com.dieti.dietiestatesbackend.repositories.AgentAvailabilityRepository;
import com.dieti.dietiestatesbackend.dto.request.VisitCreationRequestDTO;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.Visit;
import com.dieti.dietiestatesbackend.enums.VisitStatus;
import org.springframework.security.core.GrantedAuthority;

/**
 * Utility bean esposto per SpEL nelle espressioni @PreAuthorize.
 * Fornisce metodi per verificare ownership/permessi a livello di risorsa.
 */
@Component("securityUtil")
public class SecurityUtil {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    private final PropertyRepository propertyRepository;
    private final VisitRepository visitRepository;
    private final AgentAvailabilityRepository agentAvailabilityRepository;
    private final long ownerCancellationCutoffHours;
    
    @Autowired
    public SecurityUtil(PropertyRepository propertyRepository,
                        VisitRepository visitRepository,
                        AgentAvailabilityRepository agentAvailabilityRepository,
                        @Value("${cancellation.owner.cutoff-hours:24}") long ownerCancellationCutoffHours) {
        this.propertyRepository = propertyRepository;
        this.visitRepository = visitRepository;
        this.agentAvailabilityRepository = agentAvailabilityRepository;
        this.ownerCancellationCutoffHours = ownerCancellationCutoffHours;
    }

    /**
     * Verifica se l'utente autenticato può accedere ai dettagli di una proprietà.
     * Condizioni: è manager oppure è l'agente associato alla proprietà.
     */
    public boolean canAccessProperty(AppPrincipal principal, Long propertyId) {
        return checkAccessWithManagerOrCustomLogic(principal, propertyId, "canAccessProperty",
                () -> isPrincipalAgentOfProperty(principal, propertyId));
    }

    /**
     * Verifica se l'utente è manager oppure corrisponde all'agentId passato.
     */
    public boolean isAgentOrManager(AppPrincipal principal, Long agentId) {
        return checkAccessWithManagerOrCustomLogic(principal, agentId, "isAgentOrManager",
                () -> principal.getId() != null && principal.getId().equals(agentId));
    }

    private boolean isPrincipalAgentOfProperty(AppPrincipal principal, Long propertyId) {
        if (propertyId == null) return false;
        return propertyRepository.findById(propertyId)
                .map(p -> {
                    Long agentId = p.getAgent() != null ? p.getAgent().getId() : null;
                    return agentId != null && agentId.equals(principal.getId());
                })
                .orElse(false);
    }

    /**
     * Verifica se il principal passato può visualizzare entità correlate all'agente.
     */
    public boolean canViewAgentRelatedEntities(AppPrincipal principal, Long agentId) {
        return checkAccessWithManagerOrCustomLogic(principal, agentId, "canViewAgentRelatedEntities",
                () -> principal.getId().equals(agentId));
    }

    /**
     * Verifica se il principal può gestire uno slot di disponibilità.
     * Autorizzati: manager o l'agente proprietario dello slot.
     */
    public boolean canManageAgentAvailability(AppPrincipal principal, Long availabilityId) {
        return checkAccessWithManagerOrCustomLogic(principal, availabilityId, "canManageAgentAvailability",
                () -> agentAvailabilityRepository.findById(availabilityId)
                        .map(a -> principal.getId().equals(a.getAgent() != null ? a.getAgent().getId() : null))
                        .orElse(false));
    }
 
    /**
     * Centralizza la logica che decide se un utente autenticato può creare una visita.
     * Regola: manager sempre autorizzato, user (ROLE_USER) autorizzato per la propria visita.
     */
    public boolean canCreateVisit(AppPrincipal principal, VisitCreationRequestDTO request) {
        logger.debug("canCreateVisit - principal: {}, request: {}", principal, request);
        if (principal == null || request == null) {
            return logAndReturnFalse("canCreateVisit", "principal o request null");
        }
        if (principal.isManager()) {
            return logAndReturnTrue("canCreateVisit", "principal è manager");
        }
        boolean hasUserRole = hasRole(principal, "ROLE_USER");
        return hasUserRole && principal.getId() != null;
    }
 
    /**
     * Centralizza la logica che decide se un utente può aggiornare lo stato di una visita.
     * Regole: manager sempre, CONFIRMED|REJECTED solo agente, CANCELLED delega a canCancelVisit.
     */
    public boolean canUpdateVisitStatus(AppPrincipal principal, Long visitId, VisitStatus newStatus) {
        logger.debug("canUpdateVisitStatus - principal: {}, visitId: {}, newStatus: {}", principal, visitId, newStatus);
        if (!isValidPrincipal(principal) || visitId == null) {
            return false;
        }
        if (principal.isManager()) {
            return logAndReturnTrue("canUpdateVisitStatus", "principal è manager");
        }
 
        if (newStatus == VisitStatus.CONFIRMED || newStatus == VisitStatus.REJECTED) {
            return visitRepository.findById(visitId)
                    .map(visit -> principal.getId().equals(extractAgentId(visit)))
                    .orElse(false);
        }
 
        if (newStatus == VisitStatus.CANCELLED) {
            return canCancelVisit(principal, visitId);
        }
 
        return false;
    }
 
    /**
     * Controlla se il principal può visualizzare le visite associate ad una proprietà.
     * Regole: manager sempre, agente associato alla proprietà sì.
     */
    public boolean canViewPropertyVisits(AppPrincipal principal, Long propertyId) {
        return checkAccessWithManagerOrCustomLogic(principal, propertyId, "canViewPropertyVisits",
                () -> isPrincipalAgentOfProperty(principal, propertyId));
    }

    /**
     * Controllo per i contratti: solo i manager possono accedere.
     */
    public boolean canAccessContract(AppPrincipal principal, Long contractId) {
        logger.debug("canAccessContract - principal: {}, contractId: {}", principal, contractId);
        return principal != null && principal.isManager();
    }
    
    /**
     * Controlla se l'utente autenticato può cancellare la visita identificata da visitId.
     * Regole implementate (Vincoli 9, 10, 11):
     *  - Manager: sempre autorizzati (Vincolo 10)
     *  - Agente: sempre autorizzato per le proprie visite (Vincolo 10)
     *  - Proprietario/utente:
     *      - Visite PENDING: sempre autorizzato (Vincolo 11)
     *      - Visite CONFIRMED: solo se mancano più di 24 ore (Vincolo 9)
     *
     * Il metodo è difensivo: gestisce principal null, visitId null e visite mancanti.
     */
    public boolean canCancelVisit(AppPrincipal principal, Long visitId) {
        logger.debug("canCancelVisit - principal: {}, visitId: {}", principal, visitId);
 
        if (!isValidPrincipal(principal) || visitId == null) {
            return false;
        }
 
        if (principal.isManager()) {
            return logAndReturnTrue("canCancelVisit", "principal è manager");
        }
 
        return visitRepository.findById(visitId)
                .map(visit -> canPrincipalCancelVisit(principal, visit))
                .orElse(false);
    }

    /**
     * Verifica se il principal è l'agente associato all'offerta specificata.
     * Usato per autorizzare operazioni riservate all'agente (es. accettare/rifiutare offerte).
     */
    public boolean isAgentOfOffer(Long offerID, AppPrincipal principal) {
        logger.debug("isAgentOfOffer - principal: {}, offerID: {}", principal, offerID);
        if (!isValidPrincipal(principal) || offerID == null) {
            return false;
        }
        Property property = propertyRepository.findByOfferId(offerID);
        if (property == null || property.getAgent() == null) {
            return false;
        }
        return principal.getId().equals(property.getAgent().getId());
    }

    // ========== Helper methods ==========

    private boolean isValidPrincipal(AppPrincipal principal) {
        return principal != null && principal.getId() != null;
    }

    private boolean checkAccessWithManagerOrCustomLogic(AppPrincipal principal, Long resourceId,
                                                        String methodName, java.util.function.Supplier<Boolean> customLogic) {
        logger.debug("{} - principal: {}, resourceId: {}", methodName, principal, resourceId);
        if (principal == null || resourceId == null) {
            return logAndReturnFalse(methodName, "principal o resourceId null");
        }
        if (principal.isManager()) {
            return logAndReturnTrue(methodName, "principal è manager");
        }
        if (principal.getId() == null) {
            return logAndReturnFalse(methodName, "principal.id è null");
        }
        return customLogic.get();
    }

    private boolean hasRole(AppPrincipal principal, String role) {
        Collection<GrantedAuthority> authorities = principal.getAuthorities();
        return authorities != null && authorities.stream()
                .anyMatch(a -> role.equals(a.getAuthority()));
    }

    private boolean logAndReturnTrue(String context, String reason) {
        logger.debug("{} - {} -> true", context, reason);
        return true;
    }

    private boolean logAndReturnFalse(String context, String reason) {
        logger.debug("{} - {} -> false", context, reason);
        return false;
    }

    private boolean canPrincipalCancelVisit(AppPrincipal principal, Visit visit) {
        Long principalId = principal.getId();
        Long agentId = extractAgentId(visit);
        Long userId = extractUserId(visit);

        if (isPrincipalTheAgent(principalId, agentId)) {
            return true;
        }

        if (isPrincipalTheOwner(principalId, userId)) {
            return canOwnerCancelVisit(visit);
        }

        logger.debug("canCancelVisit - principal non è né agent né owner -> false");
        return false;
    }

    private Long extractAgentId(Visit visit) {
        return visit.getAgent() != null ? visit.getAgent().getId() : null;
    }

    private Long extractUserId(Visit visit) {
        return visit.getUser() != null ? visit.getUser().getId() : null;
    }

    private boolean isPrincipalTheAgent(Long principalId, Long agentId) {
        if (principalId.equals(agentId)) {
            logger.debug("canCancelVisit - principal è agente della visita (agentId: {}) -> true", agentId);
            return true;
        }
        return false;
    }

    private boolean isPrincipalTheOwner(Long principalId, Long userId) {
        return principalId.equals(userId);
    }

    private boolean canOwnerCancelVisit(Visit visit) {
        VisitStatus status = visit.getStatus();
        
        // Vincolo 11: Per visite PENDING, l'utente può sempre cancellare
        if (status == VisitStatus.PENDING) {
            logger.debug("canCancelVisit - owner can cancel PENDING visit -> true");
            return true;
        }
        
        // Vincolo 9: Per visite CONFIRMED, applicare la finestra di 24 ore
        if (status == VisitStatus.CONFIRMED) {
            return isWithin24HourWindow(visit);
        }
        
        // Per altri stati (REJECTED, CANCELLED, COMPLETED), non permettere la cancellazione
        logger.debug("canCancelVisit - owner cannot cancel visit in status: {}", status);
        return false;
    }

    private boolean isWithin24HourWindow(Visit visit) {
        Instant start = visit.getStartTime();
        if (start == null) {
            logger.debug("canCancelVisit - visit.startTime è null -> false");
            return false;
        }
        Instant cutoff = start.minus(ownerCancellationCutoffHours, ChronoUnit.HOURS);
        Instant now = Instant.now();
        boolean allowed = now.isBefore(cutoff);
        logger.debug("canCancelVisit - owner cancellation for CONFIRMED visit allowed: {}, now: {}, cutoff: {}, cutoffHours: {}",
                allowed, now, cutoff, ownerCancellationCutoffHours);
        return allowed;
    }
     
}