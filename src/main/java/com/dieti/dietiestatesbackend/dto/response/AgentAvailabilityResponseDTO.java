package com.dieti.dietiestatesbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO di risposta per uno slot di disponibilità agente.
 * I timestamp sono Instant (UTC).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentAvailabilityResponseDTO {
    private Long id;
    private Long agentId;
    private Instant startTime;
    private Instant endTime;
}