package com.dieti.dietiestatesbackend.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configurazione del provider per servizi di geocoding.
 */
@Data
@NoArgsConstructor
@ConfigurationProperties("geocoding.provider")
public class ProviderConfig {
    private GeoapifyConfig geoapify = new GeoapifyConfig();
}