package com.dieti.dietiestatesbackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("geocoding")
public class GeocodingProperties {

    private Provider provider = new Provider();

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public static class Provider {
        private Geoapify geoapify = new Geoapify();

        public Geoapify getGeoapify() {
            return geoapify;
        }

        public void setGeoapify(Geoapify geoapify) {
            this.geoapify = geoapify;
        }
    }

    public static class Geoapify {
        private String apiUrl;
        private String apiKey;

        public String getApiUrl() {
            return apiUrl;
        }

        public void setApiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
    }
}