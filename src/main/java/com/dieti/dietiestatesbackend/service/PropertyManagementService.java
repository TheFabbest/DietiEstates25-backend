package com.dieti.dietiestatesbackend.service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.context.annotation.Lazy;
import com.dieti.dietiestatesbackend.exception.InvalidImageException;
import com.dieti.dietiestatesbackend.exception.StorageException;


import com.dieti.dietiestatesbackend.dto.request.CreatePropertyRequest;
import com.dieti.dietiestatesbackend.dto.response.PropertyResponse;

import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.enums.PropertyType;
import com.dieti.dietiestatesbackend.mappers.ResponseMapperRegistry;
import com.dieti.dietiestatesbackend.repositories.PropertyRepository;
import com.dieti.dietiestatesbackend.service.storage.FileStorageService;
import com.dieti.dietiestatesbackend.service.storage.ImageValidationService;
import com.github.f4b6a3.ulid.UlidCreator;

import jakarta.persistence.PersistenceException;

/**
 * Service responsabile delle operazioni di scrittura/gestione sulle Property.
 * Metodi di creazione/aggiornamento rimossi da PropertyService sono stati
 * spostati qui e adattati per usare PropertyDependencyResolver e
 * PropertyCreationService.
 */
@Service
public class PropertyManagementService {
    private final PropertyManagementService self;

    private static final Logger logger = LoggerFactory.getLogger(PropertyManagementService.class);

    private final PropertyRepository propertyRepository;
 
    private final PropertyCreationService propertyCreationService;
    private final ValidationService validationService;
    private final ResponseMapperRegistry responseMapperRegistry;
    private final FileStorageService fileStorageService;
    private final ImageValidationService imageValidationService;


    public PropertyManagementService(PropertyRepository propertyRepository,
                                     PropertyCreationService propertyCreationService,
                                     ValidationService validationService,
                                     ResponseMapperRegistry responseMapperRegistry,
                                     FileStorageService fileStorageService,
                                     ImageValidationService imageValidationService,
                                     @Lazy PropertyManagementService self) {
        this.propertyRepository = Objects.requireNonNull(propertyRepository, "propertyRepository");
        this.propertyCreationService = Objects.requireNonNull(propertyCreationService, "propertyCreationService");
        this.validationService = Objects.requireNonNull(validationService, "validationService");
        this.responseMapperRegistry = Objects.requireNonNull(responseMapperRegistry, "responseMapperRegistry");
        this.fileStorageService = Objects.requireNonNull(fileStorageService, "fileStorageService");
        this.imageValidationService = Objects.requireNonNull(imageValidationService, "imageValidationService");
        this.self = self;
    }

    /**
     * Crea una proprietà con immagini a partire dal DTO unificato e una lista di file.
     * Implementa la strategia "Storage-First con Compensazione Sincrona":
     * 1. Prima caricamento immagini su Azure Blob Storage
     * 2. Solo se l'upload ha successo, salvataggio nel database
     * 3. In caso di fallimento del database, eliminazione delle immagini caricate (compensazione)
     */
    public PropertyResponse createPropertyWithImages(CreatePropertyRequest request, List<MultipartFile> images) {
        validationService.validate(request);
        logger.debug("Inizio creazione proprietà con immagini per categoria: {}", request.getPropertyCategoryName());
        logger.debug("Numero immagini ricevute: {}", images.size());

        // 1. Validazione "Fail-Fast" delle immagini
        for (MultipartFile image : images) {
            try {
                imageValidationService.validateImage(image.getInputStream(), image.getContentType(), image.getSize());
            } catch (IOException | IllegalArgumentException e) {
                throw new InvalidImageException("Validazione fallita per l'immagine: " + image.getOriginalFilename(), e);
            }
        }

        // 2. Generazione ULID per la directory delle immagini
        String imageDirectoryUlid = UlidCreator.getUlid().toString();
        logger.debug("Generato imageDirectoryUlid: {}", imageDirectoryUlid);

        // 3. Upload delle immagini su Azure Blob Storage (FUORI TRANSAZIONE DB)
        boolean uploadSuccess = fileStorageService.uploadImages(imageDirectoryUlid, images);
        if (!uploadSuccess) {
            logger.error("Fallito l'upload delle immagini per l'ULID: {}", imageDirectoryUlid);
            throw new StorageException("Impossibile caricare le immagini per la proprietà.");
        }
        logger.debug("Immagini caricate con successo per l'ULID: {}", imageDirectoryUlid);

        // 4. Persistenza nel database (IN UNA NUOVA TRANSAZIONE)
        Property createdProperty;
        try {
            createdProperty = self.persistProperty(request, imageDirectoryUlid, images.size());
        } catch (Exception dbException) {
            // Compensazione: elimina le immagini caricate se il salvataggio DB fallisce
            boolean deleteSuccess = fileStorageService.deleteImages(imageDirectoryUlid);
            if (!deleteSuccess) {
                logger.error("Fallita la compensazione: impossibile eliminare le immagini caricate per l'ULID: {}", imageDirectoryUlid);
                // Considerare meccanismi di retry o alerting per questo scenario
            }
            throw new PersistenceException("Errore durante la creazione della proprietà. Immagini caricate rimosse.", dbException);
        }

        return responseMapperRegistry.map(createdProperty);
    }

    @Transactional
    protected Property persistProperty(CreatePropertyRequest request, String imageDirectoryUlid, int numberOfImages) {
        Property property = propertyCreationService.createProperty(request);

        if (property.getPropertyCategory() == null) {
            throw new IllegalArgumentException("PropertyCategory is required");
        }

        property.setImageDirectoryUlid(imageDirectoryUlid);
        property.setNumberOfImages(numberOfImages);

        PropertyType derivedPropertyType = PropertyType.valueOf(property.getPropertyCategory().getPropertyType());
        logger.debug("PropertyType derivato dalla categoria: {}", derivedPropertyType);

        Property savedProperty = propertyRepository.save(property);
        logger.info("Property created with images id={}, type={}, imagesCount={}, imageDirectoryUlid={}",
                   savedProperty.getId(), derivedPropertyType, numberOfImages, imageDirectoryUlid);
        
        return savedProperty;
    }
}