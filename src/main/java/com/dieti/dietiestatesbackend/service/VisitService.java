package com.dieti.dietiestatesbackend.service;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dieti.dietiestatesbackend.dto.request.VisitCreationRequestDTO;
import com.dieti.dietiestatesbackend.dto.response.AgentVisitDTO;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.entities.Visit;
import com.dieti.dietiestatesbackend.enums.VisitStatus;
import com.dieti.dietiestatesbackend.exception.EntityNotFoundException;
import com.dieti.dietiestatesbackend.exception.InvalidPayloadException;
import com.dieti.dietiestatesbackend.repositories.PropertyRepository;
import com.dieti.dietiestatesbackend.repositories.UserRepository;
import com.dieti.dietiestatesbackend.repositories.VisitRepository;
import com.dieti.dietiestatesbackend.validation.VisitValidator;

@Service
@Transactional
public class VisitService {

    private final VisitRepository visitRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final VisitValidator visitValidator;

    // Backward-compatible constructor used in some unit tests
    public VisitService(VisitRepository visitRepository) {
        this(visitRepository, null, null, null);
    }

    @Autowired
    public VisitService(VisitRepository visitRepository,
                        PropertyRepository propertyRepository,
                        UserRepository userRepository,
                        VisitValidator visitValidator) {
        this.visitRepository = visitRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.visitValidator = visitValidator;
    }

    public Visit getVisit(Long id) {
        return visitRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Visit not found with id: " + id));
    }

    public Page<AgentVisitDTO> getAgentVisits(Long agentID, Pageable pageable) {
        return visitRepository.getAgentVisits(agentID, pageable);
    }

    /**
     * Recupera le visite richieste dall'utente specificato (paginazione delegata a Spring Data).
     */
    public Page<AgentVisitDTO> getUserVisits(Long userId, Pageable pageable) {
        return visitRepository.getUserVisits(userId, pageable);
    }

    /**
     * Recupera le visite associate a una specifica proprietà (paginazione delegata a Spring Data).
     */
    public Page<AgentVisitDTO> getPropertyVisits(Long propertyId, Pageable pageable) {
        return visitRepository.getPropertyVisits(propertyId, pageable);
    }

    /**
     * Creazione visita: le validazioni di business sono delegate al VisitValidator.
     */
    public AgentVisitDTO createVisit(VisitCreationRequestDTO req, Long requestingUserId) {
        Instant start = req.getStartTime();
        Instant end = req.getEndTime();

        // Validazioni di business centralizzate nel validator
        visitValidator.validateBusinessRules(start, end);

        Property property = findPropertyOrThrow(req.getPropertyId());
        User agent = findAgentOrThrow(req.getAgentId());
        User user = findRequestingUserOrThrow(requestingUserId);

        // Controlli di sovrapposizione e disponibilità sono delegati:
        visitValidator.ensureUserHasNoOverlap(user.getId(), start, end);
        visitValidator.ensureAgentAvailable(agent.getId(), start, end);

        Visit visit = buildVisit(property, agent, user, start, end);
        Visit saved = visitRepository.save(visit);

        return visitRepository.findAgentVisitById(saved.getId())
            .orElseThrow(() -> new EntityNotFoundException("Visit created but could not be retrieved"));
    }

    /**
     * Aggiorna lo stato di una visita seguendo le regole di transizione.
     * Le validazioni di conferma sono delegate al validator (overbooking, disponibilità).
     */
    public AgentVisitDTO updateVisitStatus(Long visitId, VisitStatus newStatus) {
        Visit visit = validateAndGetVisit(visitId);
        VisitStatus currentStatus = visit.getStatus();

        if (currentStatus == newStatus) {
            return fetchAgentVisitDTOById(visitId);
        }

        ensureTransitionAllowed(currentStatus, newStatus);

        if (currentStatus == VisitStatus.PENDING) {
            applyPendingTransition(visit, newStatus);
        } else if (currentStatus == VisitStatus.CONFIRMED && newStatus == VisitStatus.CANCELLED) {
            // Permettiamo la cancellazione di una visita precedentemente confermata.
            visit.setStatus(VisitStatus.CANCELLED);
        } else {
            throw new InvalidPayloadException(
                    Map.of("status", "Transizione di stato non valida da " + currentStatus + " a " + newStatus));
        }

        Visit saved = visitRepository.save(visit);
        return fetchAgentVisitDTOById(saved.getId());
    }

    private Visit validateAndGetVisit(Long visitId) {
        return visitRepository.findById(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Visit not found with id: " + visitId));
    }

    private void ensureTransitionAllowed(VisitStatus current, VisitStatus target) {
        if (current == VisitStatus.PENDING) {
            if (target != VisitStatus.CONFIRMED && target != VisitStatus.REJECTED && target != VisitStatus.CANCELLED) {
                throw new InvalidPayloadException(
                        Map.of("status", "Transizione di stato non valida da " + current + " a " + target));
            }
            return;
        }

        if (current == VisitStatus.CONFIRMED) {
            if (target != VisitStatus.CANCELLED) {
                throw new InvalidPayloadException(
                        Map.of("status", "Transizione di stato non valida da " + current + " a " + target));
            }
            return;
        }

        throw new InvalidPayloadException(
                Map.of("status", "Transizione di stato non valida da " + current + " a " + target));
    }

    private void applyPendingTransition(Visit visit, VisitStatus target) {
        if (target == VisitStatus.CONFIRMED) {
            // Validazioni di conferma delegate al validator (overlap utente/agente e regole di overbooking)
            visitValidator.ensureUserHasNoOverlap(visit.getUser().getId(), visit.getStartTime(), visit.getEndTime());
            visitValidator.ensureAgentAvailable(visit.getAgent().getId(), visit.getStartTime(), visit.getEndTime());
            visitValidator.ensureOverbookingRules(visit);
            visit.setStatus(VisitStatus.CONFIRMED);
        } else if (target == VisitStatus.REJECTED) {
            visit.setStatus(VisitStatus.REJECTED);
        } else if (target == VisitStatus.CANCELLED) {
            visit.setStatus(VisitStatus.CANCELLED);
        } else {
            throw new InvalidPayloadException(
                    Map.of("status", "Transizione di stato non valida da " + visit.getStatus() + " a " + target));
        }
    }

    private AgentVisitDTO fetchAgentVisitDTOById(Long id) {
        return visitRepository.findAgentVisitById(id)
                .orElseThrow(() -> new EntityNotFoundException("Visit not found after update"));
    }

    private Property findPropertyOrThrow(Long propertyId) {
        return propertyRepository.findDetailedById(propertyId)
            .orElseThrow(() -> new EntityNotFoundException("Property not found with id: " + propertyId));
    }

    private User findAgentOrThrow(Long agentId) {
        User agent = userRepository.findById(agentId)
            .orElseThrow(() -> new EntityNotFoundException("Agent (user) not found with id: " + agentId));
        if (!agent.isAgent()) {
            throw new EntityNotFoundException("User with id " + agentId + " is not an agent");
        }
        return agent;
    }

    private User findRequestingUserOrThrow(Long requestingUserId) {
        if (requestingUserId == null) {
            throw new EntityNotFoundException("Requesting user id is null");
        }
        return userRepository.findById(requestingUserId)
            .orElseThrow(() -> new EntityNotFoundException("Requesting user not found with id: " + requestingUserId));
    }

    private Visit buildVisit(Property property, User agent, User user, Instant start, Instant end) {
        Visit v = new Visit();
        v.setProperty(property);
        v.setAgent(agent);
        v.setUser(user);
        v.setStartTime(start);
        v.setEndTime(end);
        v.setStatus(VisitStatus.PENDING);
        return v;
    }
    /**
     * Cancella la visita identificata da visitId, effettuata dall'utente requestingUserId.
     *
     * Regole implementate:
     *  - La visita deve esistere e non essere già CANCELLED o COMPLETED.
     *  - Se lo stato è PENDING, sia l'utente che l'agente possono cancellare in qualsiasi momento.
     *  - Se lo stato è CONFIRMED:
     *      - L'agente (o manager) può cancellare in qualsiasi momento.
     *      - L'utente proprietario può cancellare solo se mancano più di 24 ore all'inizio della visita.
     *  - Verifica esplicita dell'autorizzazione: il requester deve essere il proprietario della visita,
     *    l'agente associato o un manager.
     *
     * In caso di violazione vengono lanciate le eccezioni:
     *  - EntityNotFoundException (visita non trovata)
     *  - UnauthorizedOperationException (caller non autorizzato)
     *  - InvalidCancellationTimeException (tentativo di cancellazione oltre la finestra consentita)
     */
    public AgentVisitDTO cancelVisit(Long visitId, Long requestingUserId) {
        Visit visit = validateAndGetVisit(visitId);
        ensureNotFinalized(visit);
 
        // Authorization and timing checks are enforced declaratively via SecurityUtil (@PreAuthorize)
        visit.setStatus(VisitStatus.CANCELLED);
        Visit saved = visitRepository.save(visit);
        return fetchAgentVisitDTOById(saved.getId());
    }

    private void ensureNotFinalized(Visit visit) {
        VisitStatus status = visit.getStatus();
        if (status == VisitStatus.CANCELLED || status == VisitStatus.COMPLETED) {
            throw new InvalidPayloadException(Map.of("status", "La visita è già nello stato: " + status));
        }
    }

}