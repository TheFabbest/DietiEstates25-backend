package com.dieti.dietiestatesbackend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dieti.dietiestatesbackend.entities.Visit;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    // Gets all offers from the agent
    @Query("SELECT v FROM Visit v " +
           "JOIN v.user u " +
           "WHERE u.isAgent = true AND u.id = :id")
    List<Visit> getAgentVisits(@Param("id") Long agentId);
}