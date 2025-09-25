package com.dieti.dietiestatesbackend.repositories;

import com.dieti.dietiestatesbackend.dto.response.AgentVisitDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dieti.dietiestatesbackend.entities.Visit;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    // Gets all visits from the agent
    @Query(value = "SELECT new com.dieti.dietiestatesbackend.dto.response.AgentVisitDTO(v, v.property.propertyCategory.propertyType, v.property.address) FROM Visit v WHERE v.property.agent.id = :agentID",
           countQuery = "SELECT count(v) FROM Visit v WHERE v.property.agent.id = :agentID")
    Page<AgentVisitDTO> getAgentVisits(@Param("agentID") Long agentID, Pageable pageable);
}