package com.dieti.dietiestatesbackend.controller;

import com.dieti.dietiestatesbackend.dto.response.PropertyResponse;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.mappers.ResponseMapperRegistry;
import com.dieti.dietiestatesbackend.service.PropertyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PropertiesControllerTest {

    @Mock
    private PropertyService propertyService;

    @Mock
    private ResponseMapperRegistry responseMapperRegistry;

    @InjectMocks
    private PropertiesController propertyController;

    @Test
    void getPropertyDetail_shouldReturnProperty_whenPropertyExists() throws Exception {
        // Given
        Long propertyId = 1L;
        Property property = new com.dieti.dietiestatesbackend.entities.ResidentialProperty();
        property.setId(propertyId);
        PropertyResponse propertyResponse = new PropertyResponse();
        propertyResponse.setId(propertyId);
        propertyResponse.setDescription("Test Property Description");

        when(propertyService.getProperty(anyLong())).thenReturn(property);
        when(responseMapperRegistry.map(any(Property.class))).thenReturn(propertyResponse);

        // When
        ResponseEntity<PropertyResponse> response = propertyController.getPropertyDetail(propertyId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(propertyResponse, response.getBody());
    }

    @Test
    void getPropertyDetail_shouldReturnNotFound_whenPropertyDoesNotExist() throws Exception {
        // Given
        Long propertyId = 1L;
        when(propertyService.getProperty(anyLong())).thenReturn(null);
        when(responseMapperRegistry.map(null)).thenReturn(null);

        // When
        ResponseEntity<PropertyResponse> response = propertyController.getPropertyDetail(propertyId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }
}