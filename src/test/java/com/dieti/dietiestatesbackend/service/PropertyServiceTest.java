package com.dieti.dietiestatesbackend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.exception.EntityNotFoundException;
import com.dieti.dietiestatesbackend.repositories.PropertyRepository;
import com.dieti.dietiestatesbackend.mappers.ResponseMapperRegistry;
import com.dieti.dietiestatesbackend.service.places.PlacesService;
import com.dieti.dietiestatesbackend.service.storage.FileStorageService;

@ExtendWith(MockitoExtension.class)
public class PropertyServiceTest {

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
}