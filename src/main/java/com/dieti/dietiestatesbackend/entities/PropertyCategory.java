package com.dieti.dietiestatesbackend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Table(name = "PropertyCategory")
//LOOK-UP TABLE
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class PropertyCategory extends BaseEntity {

    /**
     * Canonical discriminator for JPA inheritance and property type grouping.
     * Values: RESIDENTIAL, COMMERCIAL, LAND, GARAGE
     * This serves as the single source of truth for property type classification.
     */
    @NotBlank
    @Column(name = "property_type", nullable = false)
    private String propertyType;

    /**
     * Specific category name shown to users (e.g., Apartment, Villa, Office, Shop).
     * This is the granular category that users select.
     */
    @NotBlank
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "is_active")
    private boolean isActive = true;
}