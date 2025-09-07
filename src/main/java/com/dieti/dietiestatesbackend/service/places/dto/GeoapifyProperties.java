package com.dieti.dietiestatesbackend.service.places.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO che rappresenta le proprietà di un luogo nella risposta di Geoapify Places.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class GeoapifyProperties {

    @JsonProperty("name")
    private String name;

    @JsonProperty("country")
    private String country;

    @JsonProperty("city")
    private String city;

    @JsonProperty("street")
    private String street;

    @JsonProperty("housenumber")
    private String houseNumber;

    @JsonProperty("lat")
    private Double lat;

    @JsonProperty("lon")
    private Double lon;

    @JsonProperty("formatted")
    private String formattedAddress;

    @JsonProperty("categories")
    private List<String> categories;

    @JsonProperty("distance")
    private Integer distance;

    @JsonProperty("place_id")
    private String placeId;
}
