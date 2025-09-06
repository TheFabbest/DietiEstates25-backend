package com.dieti.dietiestatesbackend.service.geocoding.provider;

import com.dieti.dietiestatesbackend.config.GeocodingProperties;
import com.dieti.dietiestatesbackend.entities.Address;
import com.dieti.dietiestatesbackend.entities.Coordinates;
import com.dieti.dietiestatesbackend.exception.GeocodingException;
import com.dieti.dietiestatesbackend.service.geocoding.GeocodingService;
import com.dieti.dietiestatesbackend.service.geocoding.dto.GeoapifyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import org.springframework.context.annotation.Primary;

@Service
@Primary
public class GeoapifyGeocodingService implements GeocodingService {

    private static final Logger logger = LoggerFactory.getLogger(GeoapifyGeocodingService.class);
    private static final String CACHE_NAME = "geocoding";

    private final WebClient webClient;
    private final GeocodingProperties geocodingProperties;

    public GeoapifyGeocodingService(WebClient geoapifyWebClient, GeocodingProperties geocodingProperties) {
        this.webClient = geoapifyWebClient;
        this.geocodingProperties = geocodingProperties;
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "#address.toString()")
    public Optional<Coordinates> geocode(Address address) throws GeocodingException {
        validateAddress(address);

        logger.debug("Geocoding address: {}", address);

        return requestGeoapify(address)
                .flatMap(response -> Mono.justOrEmpty(extractCoordinates(response)))
                .onErrorResume(WebClientResponseException.class, e -> {
                    logger.error("Geoapify API request failed: {}", e.getMessage(), e);
                    HttpStatus status = e.getStatusCode().is4xxClientError() ? HttpStatus.BAD_REQUEST : HttpStatus.SERVICE_UNAVAILABLE;
                    return Mono.error(new GeocodingException("Failed to call Geoapify API: " + e.getMessage(), e, status));
                })
                .onErrorResume(Exception.class, e -> {
                    logger.error("Unexpected error during geocoding: {}", e.getMessage(), e);
                    return Mono.error(new GeocodingException("Unexpected error during geocoding: " + e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR));
                })
                .blockOptional();
    }

    private void validateAddress(Address address) {
        if (address == null) {
            throw new IllegalArgumentException("Address must not be null");
        }
    }

    private Mono<GeoapifyResponse> requestGeoapify(Address address) {
        String apiKey = geocodingProperties.getProvider().getGeoapify().getApiKey();
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("street", address.getStreet())
                        .queryParam("housenumber", address.getStreetNumber())
                        .queryParam("city", address.getCity())
                        .queryParam("country", address.getCountry())
                        .queryParam("limit", 1)
                        .build())
                .header("X-API-Key", apiKey)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        clientResponse -> {
                            logger.error("Geoapify API error: {}", clientResponse.statusCode());
                            HttpStatus status = clientResponse.statusCode().is4xxClientError() ? HttpStatus.BAD_REQUEST : HttpStatus.SERVICE_UNAVAILABLE;
                            return Mono.error(new GeocodingException(
                                    "Geoapify API returned error: " + clientResponse.statusCode(), status));
                        })
                .bodyToMono(GeoapifyResponse.class);
    }

    private Optional<Coordinates> extractCoordinates(GeoapifyResponse response) {
        if (response == null || response.getFeatures() == null || response.getFeatures().isEmpty()) {
            logger.debug("No coordinates found in Geoapify response");
            return Optional.empty();
        }

        GeoapifyResponse.Feature feature = response.getFeatures().get(0);
        GeoapifyResponse.Properties properties = feature.getProperties();

        if (properties == null || properties.getLatitude() == null || properties.getLongitude() == null) {
            logger.debug("Latitude or longitude missing in Geoapify properties");
            return Optional.empty();
        }

        Coordinates coordinates = new Coordinates(properties.getLatitude(), properties.getLongitude());
        logger.debug("Coordinates found: {}", coordinates);
        return Optional.of(coordinates);
    }
}
