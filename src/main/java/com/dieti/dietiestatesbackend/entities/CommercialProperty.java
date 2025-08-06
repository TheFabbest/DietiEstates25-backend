package com.dieti.dietiestatesbackend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "commercial_property")
public class CommercialProperty extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_property", foreignKey = @ForeignKey(name = "fk_commercialproperty_property"))
    private Property property;

    @NotNull
    @Min(1)
    @Column(name = "number_of_rooms", nullable = false)
    private Integer numberOfRooms;

    @NotNull
    @Min(1)
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

    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }

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
}