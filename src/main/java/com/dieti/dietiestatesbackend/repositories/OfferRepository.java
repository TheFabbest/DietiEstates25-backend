package com.dieti.dietiestatesbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.dieti.dietiestatesbackend.entities.Offer;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    // Gets all offers from the agent
    /**
     * Recupera tutte le offerte create da un agente specifico.
     * La query verifica che l'utente abbia il flag is_agent abilitato
     * e filtra per l'ID dell'agente per garantire che solo le offerte
     * dell'agente specifico vengano restituite.
     *
     * @param agentId L'ID dell'agente
     * @return Lista di offerte dell'agente
     */
    @Query(value = "SELECT o FROM Offer o JOIN FETCH o.property p JOIN FETCH p.agent JOIN FETCH o.user WHERE p.agent.id = :id",
           countQuery = "SELECT count(o) FROM Offer o WHERE o.property.agent.id = :id")
    Page<Offer> getAgentOffers(@Param("id") Long agentId, Pageable pageable);

    Optional<Offer> findByPropertyIdAndUserId(Long propertyId, Long userId);
}