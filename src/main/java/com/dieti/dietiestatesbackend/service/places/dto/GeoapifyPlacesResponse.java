package com.dieti.dietiestatesbackend.service.places.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
/**
 * DTO per deserializzare la risposta JSON dell'API Geoapify Places.
 * Rappresenta una FeatureCollection GeoJSON con i punti di interesse trovati.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class GeoapifyPlacesResponse {

    @JsonProperty("type")
    private String type;

    @JsonProperty("features")
    private List<GeoapifyFeature> features;
}
