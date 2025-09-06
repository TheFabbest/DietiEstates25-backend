package com.dieti.dietiestatesbackend.dto.response;

import java.util.List;

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
    private List<String> floors;
    private Integer numberOfBathrooms;
    private Integer totalFloors;
    private boolean hasDisabledAccess;
    private Integer shopWindowCount;
}