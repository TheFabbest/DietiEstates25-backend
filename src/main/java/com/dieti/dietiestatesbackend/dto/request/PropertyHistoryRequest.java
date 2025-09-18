package com.dieti.dietiestatesbackend.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO per la richiesta di cronologia immobili.
 * Contiene una lista di ID di immobili da recuperare.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyHistoryRequest {

    /**
     * Lista degli ID degli immobili da recuperare.
     * Deve essere non null e non vuoto.
     */
    @NotNull(message = "La lista degli ID immobili non può essere null")
    @NotEmpty(message = "La lista degli ID immobili non può essere vuota")
    @Size(max = 100, message = "La richiesta non può superare i 100 ID immobili")
    private List<String> propertyIds;
}