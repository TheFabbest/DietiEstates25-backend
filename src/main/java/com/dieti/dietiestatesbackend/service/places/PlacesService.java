package com.dieti.dietiestatesbackend.service.places;

import com.dieti.dietiestatesbackend.service.geocoding.Coordinates;
import com.dieti.dietiestatesbackend.service.places.dto.PlaceDTO;

import java.util.List;

/**
 * Servizio per la ricerca di punti di interesse nelle vicinanze di coordinate specifiche.
 * Astrae la logica di interazione con servizi esterni di geolocalizzazione.
 */
public interface PlacesService {

    /**
     * Trova punti di interesse nelle vicinanze delle coordinate specificate.
     *
     * @param coordinates coordinate geografiche del punto di riferimento
     * @param radius raggio di ricerca in metri
     * @param categories lista di categorie di luoghi da cercare
     * @return lista di DTO rappresentanti i luoghi trovati
     */
    List<PlaceDTO> findNearbyPlaces(Coordinates coordinates, int radius, List<String> categories);
}