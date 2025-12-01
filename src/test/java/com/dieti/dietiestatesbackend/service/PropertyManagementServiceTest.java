package com.dieti.dietiestatesbackend.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.dieti.dietiestatesbackend.dto.request.CreateCommercialPropertyRequest;
import com.dieti.dietiestatesbackend.dto.request.CreatePropertyRequest;
import com.dieti.dietiestatesbackend.dto.response.PropertyResponse;
import com.dieti.dietiestatesbackend.entities.CommercialProperty;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.PropertyCategory;
import com.dieti.dietiestatesbackend.enums.PropertyType;
import com.dieti.dietiestatesbackend.exception.InvalidImageException;
import com.dieti.dietiestatesbackend.exception.StorageException;
import com.dieti.dietiestatesbackend.mappers.ResponseMapperRegistry;
import com.dieti.dietiestatesbackend.repositories.PropertyRepository;
import com.dieti.dietiestatesbackend.service.storage.FileStorageService;
import com.dieti.dietiestatesbackend.service.storage.ImageValidationService;

import jakarta.persistence.PersistenceException;

@ExtendWith(MockitoExtension.class)
class PropertyManagementServiceTest {

    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private PropertyCreationService propertyCreationService;
    @Mock
    private ValidationService validationService;
    @Mock
    private ResponseMapperRegistry responseMapperRegistry;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private ImageValidationService imageValidationService;

    @InjectMocks
    private PropertyManagementService propertyManagementService;

    private MultipartFile mockImage() throws IOException {
        MultipartFile image = mock(MultipartFile.class);
        when(image.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[] {1, 2, 3}));
        when(image.getContentType()).thenReturn("image/png");
        when(image.getSize()).thenReturn(123L);
        return image;
    }

    @Test
    void createPropertyWithImages_success_mapsResponse() throws Exception {
        PropertyManagementService realService = new PropertyManagementService(
            propertyRepository,
            propertyCreationService,
            validationService,
            responseMapperRegistry,
            fileStorageService,
            imageValidationService,
            null  // self will be set below
        );
        PropertyManagementService spyService = spy(realService);
        ReflectionTestUtils.setField(spyService, "self", spyService);

        CreatePropertyRequest request = mock(CreateCommercialPropertyRequest.class);
        MultipartFile image = mockImage();

        Property property = mock(CommercialProperty.class);
        PropertyCategory category = mock(PropertyCategory.class);
        when(property.getPropertyCategory()).thenReturn(category);
        when(property.getPropertyCategory().getPropertyType()).thenReturn(PropertyType.COMMERCIAL.name());
        
        // stub behavior
        doNothing().when(validationService).validate(request);
        doNothing().when(imageValidationService).validateImage(any(), anyString(), anyLong());
        when(fileStorageService.uploadImages(anyString(), anyList())).thenReturn(true);
        when(propertyCreationService.createProperty(request)).thenReturn(property);
        when(propertyCreationService.createProperty(request)).thenReturn(property);
        Property saved = mock(Property.class);
        when(propertyRepository.save(property)).thenReturn(saved);
        PropertyResponse expectedResponse = mock(PropertyResponse.class);
        when(responseMapperRegistry.map(saved)).thenReturn(expectedResponse);

        PropertyResponse result = spyService.createPropertyWithImages(request, List.of(image));

        assertSame(expectedResponse, result);
        verify(validationService).validate(request); // Added verification for validation service
        verify(imageValidationService).validateImage(any(), eq("image/png"), eq(123L));
        verify(fileStorageService).uploadImages(anyString(), eq(List.of(image)));
        verify(propertyCreationService).createProperty(request); // Added verification for property creation service
        verify(propertyRepository).save(property);
        verify(responseMapperRegistry).map(saved);
        verify(fileStorageService, never()).deleteImages(anyString());
    }

@Test
void createPropertyWithImages_invalidImage_throwsInvalidImageException() throws Exception {
    CreatePropertyRequest request = mock(CreateCommercialPropertyRequest.class);
    MultipartFile image = mock(MultipartFile.class);
    when(image.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[] {}));
    when(image.getOriginalFilename()).thenReturn("bad.png");
    when(image.getContentType()).thenReturn("image/png");
    when(image.getSize()).thenReturn(10L);

    doThrow(new IllegalArgumentException("bad")).when(imageValidationService).validateImage(any(), anyString(), anyLong());

    Executable executable = () -> propertyManagementService
                                    .createPropertyWithImages(request, List.of(image));

    assertThrows(InvalidImageException.class, executable);

    verify(fileStorageService, never()).uploadImages(anyString(), anyList());
    verify(propertyRepository, never()).save(any());
}


    @Test
    void createPropertyWithImages_uploadFails_throwsStorageException() throws Exception {
        CreateCommercialPropertyRequest request = mock(CreateCommercialPropertyRequest.class);
        MultipartFile image = mockImage();

        doNothing().when(imageValidationService).validateImage(any(), anyString(), anyLong());
        when(fileStorageService.uploadImages(anyString(), anyList())).thenReturn(false);

        Executable executable =
                () -> propertyManagementService.createPropertyWithImages(request, List.of(image));

        assertThrows(StorageException.class, executable);

        verify(fileStorageService).uploadImages(anyString(), eq(List.of(image)));
        verify(propertyRepository, never()).save(any());
    }

    @Test
    void createPropertyWithImages_dbSaveFails_compensatesAndThrowsRuntimeException() throws Exception {
        CreateCommercialPropertyRequest request = mock(CreateCommercialPropertyRequest.class);
        MultipartFile image = mockImage();

        doNothing().when(imageValidationService).validateImage(any(), anyString(), anyLong());
        when(fileStorageService.uploadImages(anyString(), anyList())).thenReturn(true);

        when(fileStorageService.deleteImages(anyString())).thenReturn(true);

        Executable executable =
                () -> propertyManagementService.createPropertyWithImages(request, List.of(image));

        PersistenceException ex = assertThrows(PersistenceException.class, executable);

        assertTrue(ex.getMessage().contains("Errore durante la creazione"));

        verify(fileStorageService).uploadImages(anyString(), eq(List.of(image)));
        verify(fileStorageService).deleteImages(anyString());
    }
}
