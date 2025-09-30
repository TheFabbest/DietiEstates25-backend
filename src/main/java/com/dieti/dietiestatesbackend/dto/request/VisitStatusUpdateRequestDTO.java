package com.dieti.dietiestatesbackend.dto.request;

import com.dieti.dietiestatesbackend.enums.VisitStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

/**
 * DTO per aggiornamento dello stato di una visita
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisitStatusUpdateRequestDTO {

    @NotNull(message = "Il nuovo stato della visita è obbligatorio")
    private VisitStatus status;
}