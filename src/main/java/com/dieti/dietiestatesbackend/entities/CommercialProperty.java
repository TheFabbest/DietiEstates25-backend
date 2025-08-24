package com.dieti.dietiestatesbackend.entities;
 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
 
@Entity
@Table(name = "commercial_property")
@PrimaryKeyJoinColumn(name = "id")
public class CommercialProperty extends Property {
 
 
    @NotNull
    @Min(1)
    @Column(name = "number_of_rooms", nullable = false)
    private Integer numberOfRooms;
 
    @NotNull
    @Min(0)
    @Column(name = "floor", nullable = false)
    private Integer floor;
 
    @NotNull
    @Min(1)
    @Column(name = "number_of_bathrooms", nullable = false)
    private Integer numberOfBathrooms;
 
    @NotNull
    @Min(1)
    @Column(name = "number_of_floors", nullable = false)
    private Integer numberOfFloors;
 
    @Column(name = "wheelchair_access")
    private boolean hasWheelchairAccess = false;
 
    @Min(0)
    @Column(name = "numero_vetrine")
    private Integer numeroVetrine = 0;
 
    // Getters and setters
 
    public Integer getNumberOfRooms() { return numberOfRooms; }
    public void setNumberOfRooms(Integer numeroLocali) { this.numberOfRooms = numeroLocali; }
 
    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }
 
    public Integer getNumberOfBathrooms() { return numberOfBathrooms; }
    public void setNumberOfBathrooms(Integer numeroBagni) { this.numberOfBathrooms = numeroBagni; }
 
    public Integer getNumberOfFloors() { return numberOfFloors; }
    public void setNumberOfFloors(Integer numberOfFloors) { this.numberOfFloors = numberOfFloors; }
 
    public boolean getHasWheelchairAccess() { return hasWheelchairAccess; }
    public void setHasWheelchairAccess(boolean hasAccess) { this.hasWheelchairAccess = hasAccess; }
 
    public Integer getNumeroVetrine() { return numeroVetrine; }
    public void setNumeroVetrine(Integer numeroVetrine) { this.numeroVetrine = numeroVetrine; }
 
    @Override
    public void accept(PropertyVisitor visitor) {
        visitor.visit(this);
    }
}