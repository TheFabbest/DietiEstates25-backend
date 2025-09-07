package com.dieti.dietiestatesbackend.service.places.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO che rappresenta la geometria GeoJSON nella risposta di Geoapify Places.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class GeoapifyGeometry {

    @JsonProperty("type")
    private String type;

    @JsonProperty("coordinates")
    private List<Double> coordinates;
}
