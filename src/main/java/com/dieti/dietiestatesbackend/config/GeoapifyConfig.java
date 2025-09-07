package com.dieti.dietiestatesbackend.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configurazione specifica per l'API Geoapify.
 */
@Data
@NoArgsConstructor
@ConfigurationProperties("geocoding.provider.geoapify")
public class GeoapifyConfig {
    private String apiUrl;
    private String apiKey;
    private String placesApiUrl;
}