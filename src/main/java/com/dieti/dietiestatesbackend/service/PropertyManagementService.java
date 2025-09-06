package com.dieti.dietiestatesbackend.service;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import com.dieti.dietiestatesbackend.dto.request.CreatePropertyRequest;
import com.dieti.dietiestatesbackend.dto.response.PropertyResponse;
 
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.enums.PropertyType;
import com.dieti.dietiestatesbackend.mappers.ResponseMapperRegistry;
import com.dieti.dietiestatesbackend.repositories.PropertyRepository;

/**
 * Service responsabile delle operazioni di scrittura/gestione sulle Property.
 * Metodi di creazione/aggiornamento rimossi da PropertyService sono stati
 * spostati qui e adattati per usare PropertyDependencyResolver e
 * PropertyCreationService.
 */
@Service
@Transactional
public class PropertyManagementService {

    private static final Logger logger = LoggerFactory.getLogger(PropertyManagementService.class);

    private final PropertyRepository propertyRepository;
 
    private final PropertyCreationService propertyCreationService;
    private final ValidationService validationService;
    private final ResponseMapperRegistry responseMapperRegistry;


    public PropertyManagementService(PropertyRepository propertyRepository,
                                     PropertyCreationService propertyCreationService,
                                     ValidationService validationService,
                                     ResponseMapperRegistry responseMapperRegistry) {
        this.propertyRepository = Objects.requireNonNull(propertyRepository, "propertyRepository");
        this.propertyCreationService = Objects.requireNonNull(propertyCreationService, "propertyCreationService");
        this.validationService = Objects.requireNonNull(validationService, "validationService");
        this.responseMapperRegistry = Objects.requireNonNull(responseMapperRegistry, "responseMapperRegistry");
    }

    /**
     * Crea una proprietà a partire dal DTO unificato.
     * La costruzione dell'entità specifica è delegata a PropertyCreationService,
     * che ora risolve le dipendenze e applica i campi comuni.
     */
    public PropertyResponse createProperty(CreatePropertyRequest request) {
        validationService.validate(request);
        logger.debug("Inizio creazione proprietà per categoria: {}", request.getPropertyCategoryName());
 
        // Delego la creazione della property al nuovo servizio (il servizio si occupa ora di risolvere agent tramite SecurityContext)
        Property property = propertyCreationService.createProperty(request);
 
        if (property.getPropertyCategory() == null) {
            throw new IllegalArgumentException("PropertyCategory is required");
        }
 
        PropertyType derivedPropertyType = PropertyType.valueOf(property.getPropertyCategory().getPropertyType());
        logger.debug("PropertyType derivato dalla categoria: {}", derivedPropertyType);
 
        Property saved = propertyRepository.save(property);
        logger.info("Property created id={}, type={}", saved.getId(), derivedPropertyType);
        return responseMapperRegistry.map(saved);
    }
}