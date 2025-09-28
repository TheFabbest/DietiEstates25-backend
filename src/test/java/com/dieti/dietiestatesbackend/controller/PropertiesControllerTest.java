package com.dieti.dietiestatesbackend.controller;

import com.dieti.dietiestatesbackend.dto.request.FilterRequest;
import com.dieti.dietiestatesbackend.dto.response.PropertyResponse;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.PropertyCategory;
import com.dieti.dietiestatesbackend.entities.ResidentialProperty;
import com.dieti.dietiestatesbackend.mappers.ResponseMapperRegistry;
import com.dieti.dietiestatesbackend.service.PropertyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Collectors;

import com.dieti.dietiestatesbackend.service.lookup.CategoryLookupService;
import java.util.Arrays;
import java.util.Collections;


@ExtendWith(MockitoExtension.class)
class PropertiesControllerTest {

    @Mock
    private PropertyService propertyService;

    @Mock
    private CategoryLookupService categoryLookupService;

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

    @Test
    void getProperties_shouldReturnProperties() {
        // Given
        Property property = new com.dieti.dietiestatesbackend.entities.ResidentialProperty();
        property.setId(1L);
        PropertyResponse propertyResponse = new PropertyResponse();
        propertyResponse.setId(1L);
        propertyResponse.setDescription("Test Property Description");

        FilterRequest filters = new FilterRequest();
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);

        when(propertyService.searchPropertiesWithFilters(filters, pageable)).thenReturn(new PageImpl<>(List.of(property)));
        when(responseMapperRegistry.map(eq(property))).thenReturn(propertyResponse);

        // When
        ResponseEntity<Page<PropertyResponse>> response = propertyController.getProperties(filters, pageable);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getNumberOfElements());
        assertEquals(propertyResponse, response.getBody().getContent().get(0));
    }

    @Test
    void getPropertyTypes_shouldReturnSortedTypes() {
        // Given
        List<String> types = Arrays.asList("LAST", "FIRST", "IN_THE_MIDDLE");
        when(categoryLookupService.findDistinctActivePropertyTypes()).thenReturn(types);

        // When
        ResponseEntity<List<String>> response = propertyController.getPropertyTypes();

        // Then
        assertEquals(3, response.getBody().size());
        assertEquals(Arrays.asList("FIRST", "IN_THE_MIDDLE", "LAST"), response.getBody());
        assertEquals(org.springframework.http.HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getPropertyTypes_shouldReturnEmptyListWhenNoTypes() {
        // Given
        when(categoryLookupService.findDistinctActivePropertyTypes()).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<String>> response = propertyController.getPropertyTypes();

        // Then
        assertEquals(0, response.getBody().size());
        assertEquals(org.springframework.http.HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void getCategoriesByType_shouldReturnSortedCategories() {
        // Given
        String propertyType = "RESIDENTIAL";
        PropertyCategory pc1 = new PropertyCategory();
        pc1.setName("Apartment");
        pc1.setPropertyType(propertyType);
        PropertyCategory pc2 = new PropertyCategory();
        pc2.setName("Villa");
        pc2.setPropertyType(propertyType);
        PropertyCategory pc3 = new PropertyCategory();
        pc3.setName("Townhouse");
        pc3.setPropertyType(propertyType);
        List<PropertyCategory> categories = Arrays.asList(pc1, pc2, pc3);
        when(categoryLookupService.findByPropertyType(propertyType)).thenReturn(categories);

        // When
        ResponseEntity<List<String>> response = propertyController.getCategoriesByType(propertyType);

        // Then
        assertEquals(3, response.getBody().size());
        assertEquals(categories.stream().map(PropertyCategory::getName).sorted().collect(Collectors.toList()), response.getBody());
        assertEquals(org.springframework.http.HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteProperty_shouldDelete() throws Exception {
        // Given
        Long propertyId = 1L;
        ResidentialProperty prop = new ResidentialProperty();
        prop.setId(propertyId);
        when(propertyService.getProperty(propertyId)).thenReturn(prop);

        // When
        ResponseEntity<Void> response = propertyController.deleteProperty(propertyId, null);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

        @Test
    void deleteProperty_ofNonExistentProperty_shouldReturnNotFound() throws Exception {
        // Given
        Long propertyId = 1L;
        when(propertyService.getProperty(propertyId)).thenReturn(null);

        // When
        ResponseEntity<Void> response = propertyController.deleteProperty(propertyId, null);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getAgentProperties_shouldReturnProperties() {
        // Given
        Long agentId = 1L;
        Property property = new com.dieti.dietiestatesbackend.entities.ResidentialProperty();
        property.setId(1L);
        PropertyResponse propertyResponse = new PropertyResponse();
        propertyResponse.setId(1L);
        propertyResponse.setDescription("Test Property Description");

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);

        when(propertyService.getPropertiesByAgentId(eq(agentId), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(property)));
        when(responseMapperRegistry.map(eq(property))).thenReturn(propertyResponse);

        // When
        ResponseEntity<Page<PropertyResponse>> response = propertyController.getAgentProperties(agentId, pageable);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getNumberOfElements());
        assertEquals(propertyResponse, response.getBody().getContent().get(0));
    }
}
