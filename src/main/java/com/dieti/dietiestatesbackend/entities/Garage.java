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

@Entity
@Table(name = "garage")
public class Garage extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_property", foreignKey = @ForeignKey(name = "fk_garage_property"))
    private Property property;

    @Column(name = "has_surveillance")
    private boolean hasSurveillance = false;

    @Min(1)
    @Column(name = "number_of_floors")
    private Integer numberOfFloors = 1;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }

    public boolean isHasSurveillance() { return hasSurveillance; }
    public void setHasSurveillance(boolean hasSurveillance) { this.hasSurveillance = hasSurveillance; }

    public Integer getNumberOfFloors() { return numberOfFloors; }
    public void setNumberOfFloors(Integer numberOfFloors) { this.numberOfFloors = numberOfFloors; }
}