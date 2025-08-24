package com.dieti.dietiestatesbackend.entities;

import com.dieti.dietiestatesbackend.enums.Garden;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "residential_property")
@PrimaryKeyJoinColumn(name = "id")
public class ResidentialProperty extends Property {


    @NotNull
    @Min(1)
    @Column(name = "number_of_rooms", nullable = false)
    private Integer numberOfRooms;

    @NotNull
    @Min(1)
    @Column(name = "number_of_bathrooms", nullable = false)
    private Integer numberOfBathrooms;

    @Min(0)
    @Column(name = "parking_spaces")
    private Integer parkingSpaces = 0;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_heating", nullable = false, foreignKey = @ForeignKey(name = "fk_residential_heating"))
    private Heating heating;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "garden", nullable = false)
    private Garden garden;

    @Column(name = "is_furnished")
    private boolean isFurnished = false;

    @NotNull
    @Min(1)
    @Column(name = "floor", nullable = false)
    private Integer floor;

    @NotNull
    @Min(1)
    @Column(name = "number_of_floors", nullable = false)
    private Integer numberOfFloors;

    @Column(name = "has_elevator")
    private boolean hasElevator = false;

    // Getters and setters

    public Integer getNumberOfRooms() { return numberOfRooms; }
    public void setNumberOfRooms(Integer numberOfRooms) { this.numberOfRooms = numberOfRooms; }

    public Integer getNumberOfBathrooms() { return numberOfBathrooms; }
    public void setNumberOfBathrooms(Integer numberOfBathrooms) { this.numberOfBathrooms = numberOfBathrooms; }

    public Integer getParkingSpaces() { return parkingSpaces; }
    public void setParkingSpaces(Integer parkingSpaces) { this.parkingSpaces = parkingSpaces; }

    public Heating getHeating() { return heating; }
    public void setHeating(Heating heating) { this.heating = heating; }

    public Garden getGarden() { return garden; }
    public void setGarden(Garden garden) { this.garden = garden; }

    public boolean isFurnished() { return isFurnished; }
    public void setFurnished(boolean furnished) { isFurnished = furnished; }

    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }

    public boolean hasElevator() { return hasElevator; }
    public void setHasElevator(boolean hasElevator) { this.hasElevator = hasElevator; }

    public Integer getNumberOfFloors() { return numberOfFloors; }
    public void setNumberOfFloors(Integer numberOfFloors) { this.numberOfFloors = numberOfFloors; }

    @Override
    public void accept(PropertyVisitor visitor) {
        visitor.visit(this);
    }
}