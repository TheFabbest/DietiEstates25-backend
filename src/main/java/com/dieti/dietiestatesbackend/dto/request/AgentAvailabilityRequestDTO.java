package com.dieti.dietiestatesbackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.AssertTrue;
import java.time.Instant;

/**
 * DTO per creare/aggiornare uno slot di disponibilità agente.
 * I timestamp sono Instant (UTC).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentAvailabilityRequestDTO {

    @NotNull
    @Positive
    private Long agentId;

    @NotNull
    private Instant startTime;

    @NotNull
    private Instant endTime;

    @AssertTrue(message = "endTime must be after startTime")
    public boolean isEndAfterStart() {
        if (startTime == null || endTime == null) return true;
        return endTime.isAfter(startTime);
    }
}