package com.dieti.dietiestatesbackend.entities;
 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
 
@Entity
@Table(name = "garage")
@PrimaryKeyJoinColumn(name = "id")
public class Garage extends Property {
 
 
    @Column(name = "has_surveillance")
    private boolean hasSurveillance = false;
 
    @Min(1)
    @Column(name = "number_of_floors")
    private Integer numberOfFloors = 1;
 
    // Getters and setters
 
    public boolean isHasSurveillance() { return hasSurveillance; }
    public void setHasSurveillance(boolean hasSurveillance) { this.hasSurveillance = hasSurveillance; }
 
    public Integer getNumberOfFloors() { return numberOfFloors; }
    public void setNumberOfFloors(Integer numberOfFloors) { this.numberOfFloors = numberOfFloors; }
 
    @Override
    public void accept(PropertyVisitor visitor) {
        visitor.visit(this);
    }
}