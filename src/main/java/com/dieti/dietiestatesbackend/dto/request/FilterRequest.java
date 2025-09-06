package com.dieti.dietiestatesbackend.dto.request;

import java.math.BigDecimal;
import java.util.List;

import com.dieti.dietiestatesbackend.enums.EnergyRating;
import com.dieti.dietiestatesbackend.enums.Garden;
import com.dieti.dietiestatesbackend.enums.PropertyStatus;

import jakarta.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * FilterRequest DTO — mantengo le validazioni esistenti e sostituisco
 * i getter/setter manuali con Lombok.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilterRequest {

    // Common
    @Pattern(regexp = "COMMERCIAL|RESIDENTIAL|LAND|GARAGE", message = "Property category must be COMMERCIAL, RESIDENTIAL, LAND or GARAGE")
    private String category;

    @Pattern(regexp = "SALE|RENT", message = "Contract must be SALE or RENT")
    private String contract;

    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer minArea;
    private Integer minYearBuilt;
    private List<PropertyStatus> acceptedStatus;
    private EnergyRating minEnergyRating;

    // Commercial, Residential, Garage
    private Integer minNumberOfFloors;

    // Commercial, Residential
    private Integer minNumberOfRooms;
    private Integer minNumberOfBathrooms;

    // Residential
    private Integer minParkingSpaces;
    private String heating; // TODO see regex
    private List<Garden> acceptedGarden;
    private Boolean mustBeFurnished;
    private Boolean mustHaveElevator;

    // Commercial
    private Boolean mustHaveWheelchairAccess;
    private Integer minNumeroVetrine; // TODO see

    // Garage
    private Boolean mustHaveSurveillance;

    // Land
    private Boolean mustBeAccessibleFromStreet;
}
