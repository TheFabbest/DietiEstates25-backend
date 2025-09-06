package com.dieti.dietiestatesbackend.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO per la creazione di un terreno.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class CreateLandPropertyRequest extends AbstractCreatePropertyRequest implements CreatePropertyRequest {
    // Se il frontend fornisce informazioni specifiche per il terreno
    // (attualmente l'entità Land non ha campi specifici oltre ai default),
    // manteniamo un flag per indicare se è accessibile dalla strada.
    private Boolean hasRoadAccess;
}
