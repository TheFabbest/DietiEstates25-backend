package com.dieti.dietiestatesbackend.service.geocoding.provider;

import com.dieti.dietiestatesbackend.config.GeoapifyConfig;
import com.dieti.dietiestatesbackend.config.GeocodingProperties;
import com.dieti.dietiestatesbackend.config.ProviderConfig;
import com.dieti.dietiestatesbackend.entities.Address;
import com.dieti.dietiestatesbackend.entities.Coordinates;
import com.dieti.dietiestatesbackend.exception.GeocodingException;
import com.dieti.dietiestatesbackend.service.geocoding.dto.GeoapifyResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
public class GeoapifyGeocodingServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private GeocodingProperties geocodingProperties;

    @Mock
    private ProviderConfig provider;

    @Mock
    private GeoapifyConfig geoapifyProps;

    private GeoapifyGeocodingService service;

    @SuppressWarnings("rawtypes")
    @Mock(lenient = true)
    private WebClient.RequestHeadersUriSpec uriSpec;
    
    @SuppressWarnings("rawtypes")
    @Mock(lenient = true)
    private WebClient.RequestHeadersSpec headersSpec;
    
    @Mock(lenient = true)
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    public void setUp() {
        lenient().when(geocodingProperties.getProvider()).thenReturn(provider);
        lenient().when(provider.getGeoapify()).thenReturn(geoapifyProps);
        lenient().when(geoapifyProps.getApiKey()).thenReturn("test-key");
        lenient().when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.header(anyString(), anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

        service = new GeoapifyGeocodingService(webClient, geocodingProperties);
    }

    private void mockWebClientSuccess(GeoapifyResponse response) {
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.header(anyString(), anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(GeoapifyResponse.class)).thenReturn(Mono.just(response));
    }

    private void mockWebClientError(Throwable error) {
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.header(anyString(), anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(GeoapifyResponse.class)).thenReturn(Mono.error(error));
    }

    private Address buildAddress() {
        Address a = new Address();
        a.setStreet("Main St");
        a.setStreetNumber("1");
        a.setCity("Town");
        a.setCountry("Country");
        return a;
    }

    @Test
    public void geocode_returnsCoordinates_whenResponseHasCoordinates() {
        GeoapifyResponse response = new GeoapifyResponse();
        GeoapifyResponse.Feature feature = new GeoapifyResponse.Feature();
        GeoapifyResponse.Properties props = new GeoapifyResponse.Properties();
        props.setLatitude(BigDecimal.valueOf(12.34));
        props.setLongitude(BigDecimal.valueOf(56.78));
        feature.setProperties(props);
        response.setFeatures(List.of(feature));

        // Using the helper method for cleaner test setup
        mockWebClientSuccess(response);

        Optional<Coordinates> result = service.geocode(buildAddress());

        assertTrue(result.isPresent());
        Coordinates coords = result.get();
        assertEquals(BigDecimal.valueOf(12.34), coords.getLatitude());
        assertEquals(BigDecimal.valueOf(56.78), coords.getLongitude());
    }

    @Test
    public void geocode_throwsIllegalArgument_whenAddressIsNull() {
        assertThrows(IllegalArgumentException.class, () -> service.geocode(null));
        verifyNoInteractions(webClient);
        verify(geocodingProperties, never()).getProvider();
    }

    @Test
    public void geocode_returnsEmpty_whenResponseHasNoFeatures() {
        GeoapifyResponse response = new GeoapifyResponse();
        response.setFeatures(Collections.emptyList());

        mockWebClientSuccess(response);

        Optional<Coordinates> result = service.geocode(buildAddress());

        assertTrue(result.isEmpty());
    }

    @Test
    public void geocode_returnsEmpty_whenPropertiesMissingOrLatLonNull() {
        GeoapifyResponse response1 = new GeoapifyResponse();
        GeoapifyResponse.Feature feature1 = new GeoapifyResponse.Feature();
        feature1.setProperties(null);
        response1.setFeatures(List.of(feature1));
        
        mockWebClientSuccess(response1);

        Optional<Coordinates> r1 = service.geocode(buildAddress());
        assertTrue(r1.isEmpty());

        reset(webClient, uriSpec, headersSpec, responseSpec);
        
        GeoapifyResponse response2 = new GeoapifyResponse();
        GeoapifyResponse.Feature feature2 = new GeoapifyResponse.Feature();
        GeoapifyResponse.Properties props2 = new GeoapifyResponse.Properties();
        props2.setLatitude(null);
        props2.setLongitude(null);
        feature2.setProperties(props2);
        response2.setFeatures(List.of(feature2));
        
        mockWebClientSuccess(response2);

        Optional<Coordinates> r2 = service.geocode(buildAddress());
        assertTrue(r2.isEmpty());
    }

    @Test
    public void geocode_mapsWebClientResponseException_toGeocodingException_withBadRequestFor4xx() {
        WebClientResponseException ex = new WebClientResponseException(
                "Not Found",
                404,
                "Not Found",
                HttpHeaders.EMPTY,
                null,
                Charset.defaultCharset()
        );
        
        mockWebClientError(ex);

        GeocodingException thrown = assertThrows(GeocodingException.class, () -> service.geocode(buildAddress()));
        assertTrue(thrown.getMessage().contains("Failed to call Geoapify API"));
    }

    @Test
    public void geocode_mapsOtherExceptions_toGeocodingException_internalServerError() {
        RuntimeException rex = new RuntimeException("boom");
        
        mockWebClientError(rex);

        GeocodingException thrown = assertThrows(GeocodingException.class, () -> service.geocode(buildAddress()));
        assertTrue(thrown.getMessage().contains("Unexpected error during geocoding"));
    }
}