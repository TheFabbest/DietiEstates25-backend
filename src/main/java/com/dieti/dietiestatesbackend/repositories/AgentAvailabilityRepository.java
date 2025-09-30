package com.dieti.dietiestatesbackend.repositories;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dieti.dietiestatesbackend.entities.AgentAvailability;

public interface AgentAvailabilityRepository extends JpaRepository<AgentAvailability, Long> {

    @Query("SELECT a FROM AgentAvailability a WHERE a.agent.id = :agentId AND a.startTime <= :startTime AND a.endTime >= :endTime")
    List<AgentAvailability> findCoveringIntervalForAgent(@Param("agentId") Long agentId,
                                                         @Param("startTime") Instant startTime,
                                                         @Param("endTime") Instant endTime);

    @Query("SELECT a FROM AgentAvailability a WHERE a.agent.id = :agentId AND NOT (a.endTime <= :startTime OR a.startTime >= :endTime)")
    List<AgentAvailability> findOverlappingForAgent(@Param("agentId") Long agentId,
                                                    @Param("startTime") Instant startTime,
                                                    @Param("endTime") Instant endTime);

    List<AgentAvailability> findByAgentId(Long agentId);
}