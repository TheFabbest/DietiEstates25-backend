package com.dieti.dietiestatesbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    @Query("SELECT DISTINCT o FROM Offer o " +
        "LEFT JOIN FETCH o.user " +
        "JOIN FETCH o.property p " +
        "JOIN FETCH p.contract " +
        "JOIN FETCH p.propertyCategory " +
        "JOIN FETCH p.agent " +
        "JOIN FETCH p.address " +
        "WHERE p.agent.id = :agentId " +
        "AND p.agent.isAgent = true")
    Page<Offer> getAgentOffers(@Param("agentId") Long agentId, Pageable pageable);

    @Query("SELECT o FROM Offer o " +
        "LEFT JOIN FETCH o.user " +
        "JOIN FETCH o.property p " +
        "JOIN FETCH p.contract " +
        "JOIN FETCH p.propertyCategory " +
        "JOIN FETCH p.agent " +
        "JOIN FETCH p.address " +
        "WHERE o.user.id = :userId AND p.id = :propertyId")
    Optional<Offer> findByPropertyIdAndUserId(@Param("propertyId") Long propertyId, @Param("userId") Long userId);
    
    @Query("SELECT o FROM Offer o " +
        "JOIN FETCH o.user " +
        "JOIN FETCH o.property p " +
        "JOIN FETCH p.contract " +
        "JOIN FETCH p.propertyCategory " +
        "JOIN FETCH p.agent " +
        "JOIN FETCH p.address " +
        "WHERE o.id = :offerId")
    Optional<Offer> findByIdWithUser(@Param("offerId") Long offerId);

    @Query("SELECT DISTINCT o FROM Offer o " +
        "JOIN FETCH o.property p " +
        "JOIN FETCH p.contract " +
        "JOIN FETCH p.propertyCategory " +
        "JOIN FETCH p.agent " +
        "JOIN FETCH p.address " +
        "WHERE o.user.id = :userId")
    List<Offer> findByUserId(Long userId);
}