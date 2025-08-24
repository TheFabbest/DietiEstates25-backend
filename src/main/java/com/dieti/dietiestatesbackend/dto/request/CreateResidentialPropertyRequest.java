package com.dieti.dietiestatesbackend.dto.request;
 
import java.util.List;
 
import com.dieti.dietiestatesbackend.enums.Garden;
 
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
 
/**
 * DTO for creating a residential property.
 * Extends CreatePropertyRequest with residential-specific fields.
 */
public class CreateResidentialPropertyRequest extends CreatePropertyRequest {
 
    @NotNull @Min(1)
    private Integer numberOfRooms;
 
    @NotNull @Min(1)
    private Integer numberOfBathrooms;
 
    @Min(0)
    private Integer parkingSpaces;
 
    // The client can provide the heating type name (e.g. "Centralized", "Autonomous")
    private String heatingType;
 
    @NotNull
    private Garden garden;
 
    private boolean isFurnished;
 
    private List<String> floors;
 
    @NotNull @Min(1)
    private Integer totalFloors;
 
    private boolean hasElevator;
 
    // Getters / Setters
    public Integer getNumberOfRooms() { return numberOfRooms; }
    public void setNumberOfRooms(Integer numberOfRooms) { this.numberOfRooms = numberOfRooms; }
 
    public Integer getNumberOfBathrooms() { return numberOfBathrooms; }
    public void setNumberOfBathrooms(Integer numberOfBathrooms) { this.numberOfBathrooms = numberOfBathrooms; }
 
    public Integer getParkingSpaces() { return parkingSpaces; }
    public void setParkingSpaces(Integer parkingSpaces) { this.parkingSpaces = parkingSpaces; }
 
    public String getHeatingType() { return heatingType; }
    public void setHeatingType(String heatingType) { this.heatingType = heatingType; }
 
    public Garden getGarden() { return garden; }
    public void setGarden(Garden garden) { this.garden = garden; }
 
    public boolean isFurnished() { return isFurnished; }
    public void setFurnished(boolean furnished) { isFurnished = furnished; }
 
    public List<String> getFloors() { return floors; }
    public void setFloors(List<String> floors) { this.floors = floors; }
 
    public Integer getTotalFloors() { return totalFloors; }
    public void setTotalFloors(Integer totalFloors) { this.totalFloors = totalFloors; }
 
    public boolean hasElevator() { return hasElevator; }
    public void setHasElevator(boolean hasElevator) { this.hasElevator = hasElevator; }
}