package com.dieti.dietiestatesbackend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import com.dieti.dietiestatesbackend.validation.ValidPropertyCategory;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO for creating a commercial property.
 */
@ValidPropertyCategory
@Data
@EqualsAndHashCode(callSuper = true)
public final class CreateCommercialPropertyRequest extends CreateBuildingPropertyRequest {

    @NotNull @Min(1)
    private Integer numberOfRooms;

    @NotNull
    private Integer floor;

    @NotNull @Min(1)
    private Integer numberOfBathrooms;

    private boolean hasDisabledAccess;

    @Min(0)
    private Integer shopWindowCount;

    private Integer numberOfFloors; // Aggiunto

    @Override
    public com.dieti.dietiestatesbackend.enums.PropertyType getPropertyType() {
        return com.dieti.dietiestatesbackend.enums.PropertyType.COMMERCIAL;
    }
}