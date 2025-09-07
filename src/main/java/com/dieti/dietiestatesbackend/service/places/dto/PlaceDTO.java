package com.dieti.dietiestatesbackend.service.places.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO che rappresenta un punto di interesse (POI) restituito dal servizio Places.
 * Contiene informazioni essenziali per il client senza esporre dettagli implementativi.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDTO {
    private String name;
    private String category;
    private int distance; // in metri
    private double latitude;
    private double longitude;
}
