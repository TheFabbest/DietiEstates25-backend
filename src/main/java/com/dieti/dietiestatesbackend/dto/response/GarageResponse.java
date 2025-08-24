package com.dieti.dietiestatesbackend.dto.response;

import java.util.List;

/**
 * English response DTO translated from AutorimessaResponse.
 */
public class GarageResponse extends PropertyResponse {
    private boolean hasSurveillance;
    private List<String> floors;
    private Integer numberOfFloors;

    // Getters and setters
    public boolean isHasSurveillance() { return hasSurveillance; }
    public void setHasSurveillance(boolean hasSurveillance) { this.hasSurveillance = hasSurveillance; }

    public List<String> getFloors() { return floors; }
    public void setFloors(List<String> floors) { this.floors = floors; }

    public Integer getNumberOfFloors() { return numberOfFloors; }
    public void setNumberOfFloors(Integer numberOfFloors) { this.numberOfFloors = numberOfFloors; }
}