package com.dieti.dietiestatesbackend.entities;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Rappresenta uno slot di disponibilità dichiarato da un agente.
 * Le date sono gestite con Instant (UTC).
 */
@Entity
@Table(name = "agent_availability")
@Getter
@Setter
@NoArgsConstructor
public class AgentAvailability extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_agent", nullable = false, foreignKey = @ForeignKey(name = "fk_agentavailability_agent"))
    private User agent;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private Instant endTime;
}