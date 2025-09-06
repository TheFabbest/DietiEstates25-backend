package com.dieti.dietiestatesbackend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address extends BaseEntity {

    @NotBlank
    @Column(name = "country", nullable = false)
    private String country;

    @NotBlank
    @Column(name = "province", nullable = false)
    private String province;

    @NotBlank
    @Column(name = "city", nullable = false)
    private String city;

    @NotBlank
    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "street_number")
    private String streetNumber;

    @Column(name = "building")
    private String building;

    @NotNull
    @Embedded
    private Coordinates coordinates;

    // Mantieni toString custom (non generato da Lombok)
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (street != null) sb.append(street);
        if (streetNumber != null && !streetNumber.isEmpty()) sb.append(" ").append(streetNumber);
        if (building != null && !building.isEmpty()) sb.append(", ").append(building);
        if (city != null) sb.append(", ").append(city);
        if (province != null) sb.append(", ").append(province);
        if (country != null) sb.append(", ").append(country);
        return sb.toString();
    }
}