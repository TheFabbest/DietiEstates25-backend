package com.dieti.dietiestatesbackend.dto.response;
 
import java.util.List;
 
import com.dieti.dietiestatesbackend.enums.Garden;
 
public class ResidentialPropertyResponse extends PropertyResponse {
    private Integer numberOfRooms;
    private Integer numberOfBathrooms;
    private Integer parkingSpaces;
    private HeatingDTO heating;
    private Garden garden;
    private boolean isFurnished;
    private List<String> floors;
    private Integer totalFloors;
    private boolean hasElevator;
 
    // Nested DTO for Heating
    public static class HeatingDTO {
        private Long id;
        private String type;
        private boolean isActive;
 
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
 
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
 
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }
    }
 
    // Getters and setters
    public Integer getNumberOfRooms() { return numberOfRooms; }
    public void setNumberOfRooms(Integer numberOfRooms) { this.numberOfRooms = numberOfRooms; }
 
    public Integer getNumberOfBathrooms() { return numberOfBathrooms; }
    public void setNumberOfBathrooms(Integer numberOfBathrooms) { this.numberOfBathrooms = numberOfBathrooms; }
 
    public Integer getParkingSpaces() { return parkingSpaces; }
    public void setParkingSpaces(Integer parkingSpaces) { this.parkingSpaces = parkingSpaces; }
 
    public HeatingDTO getHeating() { return heating; }
    public void setHeating(HeatingDTO heating) { this.heating = heating; }
 
    public Garden getGarden() { return garden; }
    public void setGarden(Garden garden) { this.garden = garden; }
 
    public boolean isFurnished() { return isFurnished; }
    public void setFurnished(boolean furnished) { isFurnished = furnished; }
 
    public List<String> getFloors() { return floors; }
    public void setFloors(List<String> floors) { this.floors = floors; }
 
    public Integer getTotalFloors() { return totalFloors; }
    public void setTotalFloors(Integer totalFloors) { this.totalFloors = totalFloors; }
 
    public boolean isHasElevator() { return hasElevator; }
    public void setHasElevator(boolean hasElevator) { this.hasElevator = hasElevator; }
}