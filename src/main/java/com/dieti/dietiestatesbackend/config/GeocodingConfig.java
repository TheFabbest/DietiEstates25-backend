package com.dieti.dietiestatesbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GeocodingConfig {

    @Bean
    public WebClient geoapifyWebClient(GeocodingProperties geocodingProperties) {
        return WebClient.builder()
                .baseUrl(geocodingProperties.getProvider().getGeoapify().getApiUrl())
                .build();
    }

    @Bean
    public WebClient geoapifyPlacesWebClient(GeocodingProperties geocodingProperties) {
        return WebClient.builder()
                .baseUrl(geocodingProperties.getProvider().getGeoapify().getPlacesApiUrl())
                .build();
    }
}