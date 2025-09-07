
package com.dieti.dietiestatesbackend.service.places;

import com.dieti.dietiestatesbackend.config.GeocodingProperties;
import com.dieti.dietiestatesbackend.exception.PlacesServiceException;
import com.dieti.dietiestatesbackend.service.geocoding.Coordinates;
import com.dieti.dietiestatesbackend.service.places.dto.GeoapifyPlacesResponse;
import com.dieti.dietiestatesbackend.service.places.dto.PlaceDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementazione del servizio Places che utilizza l'API Geoapify Places.
 * Segue i principi di Clean Code evitando l'uso di instanceof e utilizzando
 * deserializzazione typesafe dei DTO.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeoapifyPlacesService implements PlacesService {

    private final WebClient geoapifyPlacesWebClient;
    private final GeocodingProperties geocodingProperties;

    /**
     * Trova punti di interesse nelle vicinanze delle coordinate specificate.
     * Utilizza l'API Geoapify Places con deserializzazione typesafe.
     *
     * @param coordinates coordinate geografiche del punto di riferimento
     * @param radius raggio di ricerca in metri
     * @param categories lista di categorie di luoghi da cercare
     * @return lista di DTO rappresentanti i luoghi trovati
     * @throws PlacesServiceException se si verifica un errore durante la chiamata all'API
     */
    @Override
    public List<PlaceDTO> findNearbyPlaces(Coordinates coordinates, int radius, List<String> categories) {
        validateInputParameters(coordinates, radius, categories);

        try {
            String apiUrl = buildApiUrl(coordinates, radius, categories);
            log.debug("Chiamando API Geoapify Places: {}", apiUrl);

            GeoapifyPlacesResponse response = geoapifyPlacesWebClient.get()
                    .uri(apiUrl)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(
                        status -> status.isError(),
                        clientResponse -> Mono.error(
                            new PlacesServiceException("Errore nella chiamata all'API Geoapify Places: " + clientResponse.statusCode())
                        )
                    )
                    .bodyToMono(GeoapifyPlacesResponse.class)
                    .block();

            return mapResponseToPlaceDTOs(response);
        } catch (Exception e) {
            log.error("Errore durante la ricerca di luoghi vicini con Geoapify", e);
            throw new PlacesServiceException("Impossibile recuperare i luoghi vicini", e);
        }
    }

    /**
     * Valida i parametri di input per garantire la correttezza dei dati.
     *
     * @param coordinates coordinate da validare
     * @param radius raggio da validare
     * @param categories categorie da validare
     */
    private void validateInputParameters(Coordinates coordinates, int radius, List<String> categories) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Le coordinate non possono essere nulle");
        }
        if (radius <= 0) {
            throw new IllegalArgumentException("Il raggio di ricerca deve essere maggiore di 0");
        }
        if (categories == null || categories.isEmpty()) {
            throw new IllegalArgumentException("La lista delle categorie non può essere vuota");
        }
    }

    /**
     * Costruisce l'URL per la chiamata all'API Geoapify Places.
     *
     * @param coordinates coordinate del punto di riferimento
     * @param radius raggio di ricerca in metri
     * @param categories categorie di luoghi da cercare
     * @return URL completo per la chiamata API
     */
    private String buildApiUrl(Coordinates coordinates, int radius, List<String> categories) {
        return UriComponentsBuilder.newInstance()
                .path("")
                .queryParam("categories", String.join(",", categories))
                .queryParam("filter", String.format("circle:%f,%f,%d",
                    coordinates.longitude().doubleValue(), coordinates.latitude().doubleValue(), radius))
                .queryParam("bias", String.format("proximity:%f,%f",
                    coordinates.longitude().doubleValue(), coordinates.latitude().doubleValue()))
                .queryParam("limit", 20)
                .queryParam("apiKey", geocodingProperties.getProvider().getGeoapify().getApiKey())
                .build()
                .toUriString();
    }

    /**
     * Mappa la risposta dell'API Geoapify in una lista di PlaceDTO.
     * Utilizza deserializzazione typesafe senza uso di instanceof.
     *
     * @param response risposta dell'API Geoapify
     * @return lista di PlaceDTO mappati
     */
    private List<PlaceDTO> mapResponseToPlaceDTOs(GeoapifyPlacesResponse response) {
        if (response == null || response.getFeatures() == null) {
            return List.of();
        }

        return response.getFeatures().stream()
                .filter(feature -> feature.getProperties() != null)
                .map(feature -> {
                    var properties = feature.getProperties();
                    return new PlaceDTO(
                        properties.getName(),
                        getPrimaryCategory(properties.getCategories()),
                        properties.getDistance(),
                        properties.getLat(),
                        properties.getLon()
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Estrae la categoria principale dalla lista di categorie.
     *
     * @param categories lista di categorie
     * @return la categoria principale o "unknown" se non disponibile
     */
    private String getPrimaryCategory(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return "unknown";
        }
        return categories.get(0);
    }
}