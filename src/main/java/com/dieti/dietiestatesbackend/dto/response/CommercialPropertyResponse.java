package com.dieti.dietiestatesbackend.dto.response;

import java.util.List;

/**
 * English response DTO translated from ImmobileCommercialeResponse.
 */
public class CommercialPropertyResponse extends PropertyResponse {
    private Integer numberOfRooms;
    private List<String> floors;
    private Integer numberOfBathrooms;
    private Integer totalFloors;
    private boolean hasDisabledAccess;
    private Integer shopWindowCount;

    // Getters and setters
    public Integer getNumberOfRooms() { return numberOfRooms; }
    public void setNumberOfRooms(Integer numberOfRooms) { this.numberOfRooms = numberOfRooms; }

    public List<String> getFloors() { return floors; }
    public void setFloors(List<String> floors) { this.floors = floors; }

    public Integer getNumberOfBathrooms() { return numberOfBathrooms; }
    public void setNumberOfBathrooms(Integer numberOfBathrooms) { this.numberOfBathrooms = numberOfBathrooms; }

    public Integer getTotalFloors() { return totalFloors; }
    public void setTotalFloors(Integer totalFloors) { this.totalFloors = totalFloors; }

    public boolean isHasDisabledAccess() { return hasDisabledAccess; }
    public void setHasDisabledAccess(boolean hasDisabledAccess) { this.hasDisabledAccess = hasDisabledAccess; }

    public Integer getShopWindowCount() { return shopWindowCount; }
    public void setShopWindowCount(Integer shopWindowCount) { this.shopWindowCount = shopWindowCount; }
}