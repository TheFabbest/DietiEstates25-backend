package com.dieti.dietiestatesbackend.entities;
 
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
 
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
 
@Entity
@Table(name = "garage")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("GARAGE")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Garage extends Property {

    @Column(name = "has_surveillance")
    private boolean hasSurveillance = false;

    @Column(name = "floor")
    private Integer floor;

    @Min(1)
    @Column(name = "number_of_floors")
    private Integer numberOfFloors = 1;

}