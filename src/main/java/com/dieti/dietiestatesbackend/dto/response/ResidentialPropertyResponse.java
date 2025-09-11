package com.dieti.dietiestatesbackend.dto.response;
 
import com.dieti.dietiestatesbackend.enums.Garden;
 
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResidentialPropertyResponse extends PropertyResponse {
    private Integer numberOfRooms;
    private Integer numberOfBathrooms;
    private Integer parkingSpaces;
    private HeatingDTO heating;
    private Garden garden;
    private boolean isFurnished;
    private Integer floor;
    private Integer numberOfFloors;
    private boolean hasElevator;
}