package com.dieti.dietiestatesbackend.service.places.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO che rappresenta un singolo Feature GeoJSON nella risposta di Geoapify Places.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class GeoapifyFeature {

    @JsonProperty("type")
    private String type;

    @JsonProperty("geometry")
    private GeoapifyGeometry geometry;

    @JsonProperty("properties")
    private GeoapifyProperties properties;
}
