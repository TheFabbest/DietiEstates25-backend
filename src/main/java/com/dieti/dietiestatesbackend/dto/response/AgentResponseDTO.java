package com.dieti.dietiestatesbackend.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * DTO pubblico per rappresentare un agente nella risposta di una proprietà.
 * Espone solo campi non sensibili necessari alla presentazione.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    /**
     * Rappresentazione ridotta dell'agenzia: solo id e nome per evitare di esporre indirizzi o altri dettagli.
     */
    private Long agencyId;
    private String agencyName;
}