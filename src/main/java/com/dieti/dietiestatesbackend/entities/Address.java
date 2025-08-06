package com.dieti.dietiestatesbackend.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "indirizzo")
@SequenceGenerator(
    name = "indirizzo_seq",
    sequenceName = "dieti_estates.indirizzo_id_seq",
    allocationSize = 1
)
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
    private String street_number;

    @Column(name = "building")
    private String building;

    @NotNull
    @Digits(integer = 10, fraction = 8)
    @Column(name = "latitude", nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    @NotNull
    @Digits(integer = 11, fraction = 8)
    @Column(name = "longitude", nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    // Getters and setters
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getStreet_number() { return street_number; }
    public void setStreet_number(String street_number) { this.street_number = street_number; }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (street != null) sb.append(street);
        if (street_number != null && !street_number.isEmpty()) sb.append(" ").append(street_number);
        if (building != null && !building.isEmpty()) sb.append(", ").append(building);
        if (city != null) sb.append(", ").append(city);
        if (province != null) sb.append(", ").append(province);
        if (country != null) sb.append(", ").append(country);
        return sb.toString();
    }
}