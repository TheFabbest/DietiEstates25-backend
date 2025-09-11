package com.dieti.dietiestatesbackend.dto.request;

import java.math.BigDecimal;
import java.util.List;

import com.dieti.dietiestatesbackend.enums.EnergyRating;
import com.dieti.dietiestatesbackend.enums.PropertyCondition;
import com.dieti.dietiestatesbackend.validation.ExistingEntity;

import lombok.Data;
import com.dieti.dietiestatesbackend.entities.PropertyCategory;

/**
 * Classe base che contiene i campi comuni alle richieste di creazione proprietà.
 * yearBuilt non è qui: viene mantenuto solo nelle DTO che lo usano (residential/commercial/garage).
 */
@Data
public abstract class AbstractCreatePropertyRequest {

    // Campi comuni a tutte le proprietà (senza yearBuilt)
    private String description;
    private BigDecimal price;
    private Integer area;
    private String contractType;

    // Nome della categoria scelto dal client (rimane necessario per risolvere PropertyCategory)
    @ExistingEntity(entityClass = PropertyCategory.class, fieldName = "name", message = "La categoria proprietà specificata non esiste.")
    private String propertyCategoryName;
    
    private PropertyCondition condition;
    private EnergyRating energyRating;
    
    private AddressRequest addressRequest;
    private List<String> images;
}
