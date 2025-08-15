package com.dieti.dietiestatesbackend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dieti.dietiestatesbackend.entities.Offer;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    // Gets all offers from the agent
    @Query("SELECT o FROM Offer o " +
           "JOIN o.user u " +
           "WHERE u.isAgent = true AND u.id = :id")
    List<Offer> getAgentOffers(@Param("id") Long agentId);
}