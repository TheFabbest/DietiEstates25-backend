package com.dieti.dietiestatesbackend.dto.response;

/**
 * English response DTO translated from TerrenoResponse.
 */
public class LandResponse extends PropertyResponse {
    private boolean hasRoadAccess;

    // Getters and setters
    public boolean isHasRoadAccess() { return hasRoadAccess; }
    public void setHasRoadAccess(boolean hasRoadAccess) { this.hasRoadAccess = hasRoadAccess; }
}