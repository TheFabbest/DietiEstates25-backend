package com.dieti.dietiestatesbackend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.dieti.dietiestatesbackend.dto.request.PropertyHistoryRequest;
import com.dieti.dietiestatesbackend.dto.response.PropertyResponse;
import com.dieti.dietiestatesbackend.entities.CommercialProperty;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.exception.EntityNotFoundException;
import com.dieti.dietiestatesbackend.repositories.PropertyRepository;
import com.dieti.dietiestatesbackend.mappers.ResponseMapperRegistry;
import com.dieti.dietiestatesbackend.service.places.PlacesService;
import com.dieti.dietiestatesbackend.service.storage.FileStorageService;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private PropertyQueryServiceInterface propertyQueryServiceInterface;
    @Mock
    private PropertyManagementService propertyManagementService;
    @Mock
    private PlacesService placesService;
    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private ResponseMapperRegistry responseMapperRegistry;
    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private PropertyService propertyService;

    @Test
    void deletePropertyTest_propertyExistsAndDeleted() {
        // Given
        Long propertyId = 1L;
        Property property = mock(Property.class);
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(property.getImageDirectoryUlid()).thenReturn("someUlid");
        when(fileStorageService.deleteImages(anyString())).thenReturn(true);

        // When
        propertyService.deleteProperty(propertyId);

        // Then
        verify(propertyRepository).findById(propertyId);
        verify(fileStorageService).deleteImages("someUlid");
        verify(propertyRepository).deleteById(propertyId);
    }

    
    @Test
    void deletePropertyTest_propertyNotFound() {
        // Given
        Long propertyId = 1L;
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> propertyService.deleteProperty(propertyId));
        verify(propertyRepository).findById(propertyId);
        verifyNoInteractions(fileStorageService);
        verify(propertyRepository, never()).deleteById(anyLong());
    }

    @Test
    void deletePropertyTest_imageDeletionFails() {
        // Given
        Long propertyId = 1L;
        Property property = mock(Property.class);
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(property.getImageDirectoryUlid()).thenReturn("someUlid");
        when(fileStorageService.deleteImages(anyString())).thenReturn(false); // Simulate failure

        // When & Then
        assertDoesNotThrow(() -> propertyService.deleteProperty(propertyId));
        verify(propertyRepository).findById(propertyId);
        verify(fileStorageService).deleteImages("someUlid");
        verify(propertyRepository).deleteById(anyLong()); // Should delete even if image deletion fails
    }

    @Test
    void getPropertiesByAgentIdTest_withPagination() {
        // Given
        Long agentId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Property property1 = mock(Property.class);
        Property property2 = mock(Property.class);
        Page<Property> propertyPage = new PageImpl<>(List.of(property1, property2), pageable, 2);
        
        when(propertyRepository.getPropertiesByAgentId(agentId, pageable)).thenReturn(propertyPage);

        // When
        Page<Property> result = propertyService.getPropertiesByAgentId(agentId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());
        
        verify(propertyRepository).getPropertiesByAgentId(agentId, pageable);
    }

    @Test
    void getPropertyHistoryTest() {
        // Given
        Long propertyId = 1L;
        Property property = new CommercialProperty();
        PropertyHistoryRequest req = new PropertyHistoryRequest(List.of(propertyId.toString()));
        when(propertyQueryServiceInterface.getPropertiesByIds(anyList())).thenReturn(List.of(property));

        // When
        List<PropertyResponse> result = propertyService.getPropertyHistory(req);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(propertyQueryServiceInterface).getPropertiesByIds(List.of(propertyId.toString()));
    }
}

