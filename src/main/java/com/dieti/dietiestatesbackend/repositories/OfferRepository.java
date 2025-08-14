package com.dieti.dietiestatesbackend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dieti.dietiestatesbackend.entities.Offer;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    // gets all offers from the agent
    @Query("SELECT o FROM OFFER o " +
           "JOIN USER u ON o.id_user = u.id AND u.is_agent")
    List<Offer> getAgentOffers(@Param("id") Long agentId);
}