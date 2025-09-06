package com.dieti.dietiestatesbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.dieti.dietiestatesbackend.dto.request.AbstractCreatePropertyRequest;
import com.dieti.dietiestatesbackend.dto.request.CreatePropertyRequest;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.mappers.CreationMapperRegistry;
import com.dieti.dietiestatesbackend.security.AuthenticatedUser;
import com.dieti.dietiestatesbackend.service.lookup.AgentLookupService;
import com.dieti.dietiestatesbackend.repositories.PropertyRepository;
import com.dieti.dietiestatesbackend.exception.EntityNotFoundException;

/**
 * Crea entità Property dai vari DTO di creazione.
 * Ora il mapper si occupa della risoluzione delle dipendenze.
 */
@Service
public class PropertyCreationService {

    private static final Logger logger = LoggerFactory.getLogger(PropertyCreationService.class);

    private final AgentLookupService agentLookupService;
    private final PropertyRepository propertyRepository;
    private final CreationMapperRegistry creationMapperRegistry;


    public PropertyCreationService(AgentLookupService agentLookupService,
                                   PropertyRepository propertyRepository,
                                   CreationMapperRegistry creationMapperRegistry) {
        this.agentLookupService = agentLookupService;
        this.propertyRepository = propertyRepository;
        this.creationMapperRegistry = creationMapperRegistry;
    }

    @Transactional
    public Property createProperty(CreatePropertyRequest request) {
        // La validazione della coerenza tra propertyType e propertyCategoryName
        // è stata spostata al validatore di classe @ValidPropertyCategory.
        // Qui il servizio si occupa solo della creazione e persistenza.

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        User agent = agentLookupService.findAgentByUsername(authenticatedUser.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Agent not found with username: " + authenticatedUser.getUsername()));

        // Il mapping verso l'entità concreta è responsabilità del CreationMapperRegistry
        Property property = creationMapperRegistry.map((AbstractCreatePropertyRequest) request, agent);
        logger.debug("Property mappata prima del salvataggio: {}", property); // Log della Property mappata

        property = propertyRepository.save(property);
        logger.debug("Property salvata con successo id={} type={}", property.getId(), property.getClass().getSimpleName()); // Nuovo log

        logger.debug("Created property id={} type={}", property.getId(), property.getClass().getSimpleName());
        return property;
    }
}
