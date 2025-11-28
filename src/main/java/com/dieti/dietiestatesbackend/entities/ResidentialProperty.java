package com.dieti.dietiestatesbackend.entities;

import com.dieti.dietiestatesbackend.enums.Garden;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
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

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

/**
 * Residential property entity.
 * Use Lombok for boilerplate while keeping JPA-friendly protected no-arg constructor.
 * Avoid @ToString to prevent accidental lazy-loading of associations.
 */
@Entity
@Table(name = "residential_property")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("RESIDENTIAL")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_heating", nullable = false, foreignKey = @ForeignKey(name = "fk_residential_heating"))
    private Heating heating;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "garden", nullable = false)
    private Garden garden;

    @Column(name = "is_furnished")
    private boolean isFurnished = false;

    @NotNull
    @Min(0)
    @Column(name = "floor", nullable = false)
    private Integer floor;

    @NotNull
    @Min(1)
    @Column(name = "number_of_floors", nullable = false)
    private Integer numberOfFloors;

    @Column(name = "has_elevator")
    private boolean hasElevator = false;

    // Compatibility: existing code expects a hasElevator() accessor (not isHasElevator)
    public boolean hasElevator() { return this.hasElevator; }

}