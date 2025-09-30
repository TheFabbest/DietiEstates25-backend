package com.dieti.dietiestatesbackend.dto.request;
 
import java.time.Instant;
 
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
 
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
 
/**
 * DTO per la creazione di una nuova visita
 * Utilizza Instant per gestire i timestamp in UTC secondo il piano architetturale
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisitCreationRequestDTO {
 
    @NotNull(message = "L'ID della proprietà è obbligatorio")
    @Positive(message = "L'ID della proprietà deve essere un valore positivo")
    private Long propertyId;
 
    @NotNull(message = "L'ID dell'agente è obbligatorio")
    @Positive(message = "L'ID dell'agente deve essere un valore positivo")
    private Long agentId;
 
    @NotNull(message = "L'orario di inizio della visita è obbligatorio")
    @Future(message = "L'orario di inizio della visita deve essere futuro")
    private Instant startTime;
 
    @NotNull(message = "L'orario di fine della visita è obbligatorio")
    @Future(message = "L'orario di fine della visita deve essere futuro")
    private Instant endTime;
}