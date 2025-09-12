package com.dieti.dietiestatesbackend.repositories;

import java.util.List;
import com.dieti.dietiestatesbackend.dto.response.AgentVisitDTO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dieti.dietiestatesbackend.entities.Visit;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    // Gets all visits from the agent
    @Query("SELECT new com.dieti.dietiestatesbackend.dto.response.AgentVisitDTO(v, v.property.propertyCategory.propertyType, v.property.address) FROM Visit v WHERE v.property.agent.id = :agentID")
    List<AgentVisitDTO> getAgentVisits(@Param("agentID") Long agentID);
}