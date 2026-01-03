package com.dieti.dietiestatesbackend.dto.request;

import com.dieti.dietiestatesbackend.enums.Garden;
import com.dieti.dietiestatesbackend.entities.Heating;
import com.dieti.dietiestatesbackend.validation.ExistingEntity;
import com.dieti.dietiestatesbackend.validation.ValidPropertyCategory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO for creating a residential property.
 * Estende AbstractCreatePropertyRequest (campi comuni) e implementa CreatePropertyRequest per la deserializzazione polimorfica.
 */
@ValidPropertyCategory
@Data
@EqualsAndHashCode(callSuper = true)
public final class CreateResidentialPropertyRequest extends CreateBuildingPropertyRequest {

    @NotNull @Min(1)
    private Integer numberOfRooms;

    @NotNull @Min(1)
    private Integer numberOfBathrooms;

    @Min(0)
    private Integer parkingSpaces;

    // The client can provide the heating type name (e.g. "Centralized", "Autonomous")
    @ExistingEntity(entityClass = Heating.class, fieldName = "name", message = "Il tipo di riscaldamento specificato non esiste.")
    private String heatingType;

    @NotNull
    private Garden garden;

    private boolean isFurnished;

    private boolean hasElevator;


    // Compatibility accessor: some callers used hasElevator() previously.
    public boolean hasElevator() {
        return this.hasElevator;
    }

    @Override
    public com.dieti.dietiestatesbackend.enums.PropertyType getPropertyType() {
        return com.dieti.dietiestatesbackend.enums.PropertyType.RESIDENTIAL;
    }
}