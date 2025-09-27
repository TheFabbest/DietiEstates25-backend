package com.dieti.dietiestatesbackend.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * English response DTO translated from ImmobileCommercialeResponse.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommercialPropertyResponse extends PropertyResponse {
    private Integer numberOfRooms;
    private Integer floor;
    private Integer numberOfBathrooms;
    private Integer totalFloors;
    private boolean hasDisabledAccess;
}