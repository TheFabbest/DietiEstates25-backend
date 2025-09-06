package com.dieti.dietiestatesbackend.entities;
 
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
 
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
 
/**
 * Commercial property entity.
 * Lombok provides getters/setters and a JPA-friendly no-arg constructor.
 * Keep a compatibility accessor for callers expecting getHasWheelchairAccess().
 */
@Entity
@Table(name = "commercial_property")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("COMMERCIAL")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
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
 
    // Compatibility accessor: existing code expects getHasWheelchairAccess()
    public boolean getHasWheelchairAccess() { return this.hasWheelchairAccess; }
 
}