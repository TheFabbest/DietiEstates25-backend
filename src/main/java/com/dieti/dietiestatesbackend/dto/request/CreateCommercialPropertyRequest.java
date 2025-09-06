package com.dieti.dietiestatesbackend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO for creating a commercial property.
 */
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
}