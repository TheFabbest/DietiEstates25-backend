package com.dieti.dietiestatesbackend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dieti.dietiestatesbackend.entities.Visit;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    // Gets all visits from the agent
    @Query(value = "SELECT v.* FROM visits v " +
           "JOIN users u ON v.user_id = u.id " +
           "WHERE u.is_agent = true AND u.id = :id", nativeQuery = true)
    List<Visit> getAgentVisits(@Param("id") Long agentId);
}