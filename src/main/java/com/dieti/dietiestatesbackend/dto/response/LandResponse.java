package com.dieti.dietiestatesbackend.dto.response;

/**
 * English response DTO translated from TerrenoResponse.
 */
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LandResponse extends PropertyResponse {
    private boolean hasRoadAccess;
}