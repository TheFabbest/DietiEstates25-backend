package com.dieti.dietiestatesbackend.dto.request;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO per inviare un indirizzo inline nella richiesta di creazione proprietà.
 * Corrisponde a [`src/main/java/com/dieti/dietiestatesbackend/dto/request/IndirizzoRequest.java:1`]
 */
public class AddressRequest {

    @NotBlank
    private String country;

    @NotBlank
    private String province;

    @NotBlank
    private String city;

    @NotBlank
    private String street;

    @JsonProperty("streetNumber")
    private String streetNumber;

    private String building;

    @NotNull
    @Digits(integer = 10, fraction = 8)
    private BigDecimal latitude;

    @NotNull
    @Digits(integer = 11, fraction = 8)
    private BigDecimal longitude;

    // Getters / Setters
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public String getStreetNumber() { return streetNumber; }
    public void setStreetNumber(String streetNumber) { this.streetNumber = streetNumber; }
}