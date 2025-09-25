package com.dieti.dietiestatesbackend.service;
 
import java.util.List;
import java.util.Objects;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import com.dieti.dietiestatesbackend.dto.request.CreatePropertyRequest;
import com.dieti.dietiestatesbackend.dto.request.FilterRequest;
import com.dieti.dietiestatesbackend.dto.request.PropertyHistoryRequest;
import com.dieti.dietiestatesbackend.dto.response.PropertyResponse;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.exception.EntityNotFoundException;
import com.dieti.dietiestatesbackend.repositories.PropertyRepository;
import com.dieti.dietiestatesbackend.service.geocoding.Coordinates;
import com.dieti.dietiestatesbackend.service.places.PlacesService;
import com.dieti.dietiestatesbackend.service.places.dto.PlaceDTO;
import com.dieti.dietiestatesbackend.mappers.ResponseMapperRegistry;
import com.dieti.dietiestatesbackend.service.storage.FileStorageService;
/**
 * Façade service per le operazioni sulle Property.
 * - Entry-point unico per i controller (mantiene l'aderenza all'architettura).
 * - Delega le query read-only a {@link PropertyQueryService}.
 * - Gestisce le operazioni transazionali di scrittura (creazione, update, delete).
 */
@Service
@Transactional
public class PropertyService {
    
    private static final Logger logger = LoggerFactory.getLogger(PropertyService.class);

    private final PropertyQueryServiceInterface propertyQueryService;
    private final PropertyManagementService propertyManagementService;
    private final PlacesService placesService;
    private final PropertyRepository propertyRepository;
    private final ResponseMapperRegistry responseMapperRegistry;
    private final FileStorageService fileStorageService;
 
    /**
     * Costruttore principale: tutte le dipendenze sono richieste.
     */
    @Autowired
    public PropertyService(PropertyQueryServiceInterface propertyQueryService,
                           PropertyManagementService propertyManagementService,
                           PlacesService placesService,
                           PropertyRepository propertyRepository,
                           ResponseMapperRegistry responseMapperRegistry,
                           FileStorageService fileStorageService) {
        this.propertyQueryService = Objects.requireNonNull(propertyQueryService, "propertyQueryService");
        this.propertyManagementService = Objects.requireNonNull(propertyManagementService, "propertyManagementService");
        this.placesService = Objects.requireNonNull(placesService, "placesService");
        this.propertyRepository = Objects.requireNonNull(propertyRepository, "propertyRepository");
        this.responseMapperRegistry = Objects.requireNonNull(responseMapperRegistry, "responseMapperRegistry");
        this.fileStorageService = Objects.requireNonNull(fileStorageService, "fileStorageService");
    }


    /**
     * Search with filters with pagination. Delegates to PropertyQueryService which executes
     * a Specification-based query with fetch joins optimized for reads.
     * Geographic filters (centerLatitude, centerLongitude, radiusInMeters) are now mandatory.
     */
    public Page<Property> searchPropertiesWithFilters(FilterRequest filters, Pageable pageable) {
        Objects.requireNonNull(filters, "filters must not be null");
        Objects.requireNonNull(pageable, "pageable must not be null");
        return propertyQueryService.searchPropertiesWithFilters(filters, pageable);
    }

    /**
     * Return a small list of featured properties (latest).
     */
    public List<Property> getFeatured() {
        return propertyQueryService.getFeatured();
    }

    /**
     * Get property detail by id.
     */
    public Property getProperty(long propertyID) {
        return propertyQueryService.getProperty(propertyID);
    }

    /**
     * Crea una proprietà a partire dal DTO unificato.
     * Metodo semplificato: delega la risoluzione a helper privati e mantiene la transazione.
     */
    public PropertyResponse createProperty(CreatePropertyRequest request) {
        return propertyManagementService.createProperty(request);
    }

    /**
     * Crea una proprietà con immagini a partire dal DTO unificato e una lista di file.
     * Delega la gestione completa al PropertyManagementService.
     */
    public PropertyResponse createPropertyWithImages(CreatePropertyRequest request, List<MultipartFile> images) {
        return propertyManagementService.createPropertyWithImages(request, images);
    }

    // Dependency resolution moved to PropertyDependencyResolver

    // Helper methods removed — use dedicated utilities if needed.

    /**
     * Trova punti di interesse nelle vicinanze di un immobile.
     *
     * @param propertyId ID dell'immobile
     * @param radius raggio di ricerca in metri
     * @param categories categorie di luoghi da cercare
     * @return lista di luoghi trovati nelle vicinanze
     */
    public List<PlaceDTO> findNearbyPlaces(Long propertyId, int radius, List<String> categories) {
        Property property = propertyQueryService.getProperty(propertyId);
        if (property == null || property.getAddress() == null || property.getAddress().getCoordinates() == null) {
            throw new IllegalArgumentException("Immobile non trovato o senza coordinate valide");
        }

        var coordinates = property.getAddress().getCoordinates();
        var geoapifyCoordinates = new Coordinates(
            coordinates.getLatitude(),
            coordinates.getLongitude()
        );

        return placesService.findNearbyPlaces(geoapifyCoordinates, radius, categories);
    }

    public Page<Property> getPropertiesByAgentId(Long agentID, Pageable pageable) {
        return propertyRepository.getPropertiesByAgentId(agentID, pageable);
    }


    @Transactional
    public void deleteProperty(Long id) {
        Objects.requireNonNull(id, "id must not be null");
        var propertyOptional = propertyRepository.findById(id);

        if (propertyOptional.isPresent()) {
            Property property = propertyOptional.get();
            String imageDir = property.getImageDirectoryUlid();
            if (imageDir != null && !imageDir.isBlank()) {
                logger.info("Avvio eliminazione immagini per la proprietà {} (ULID: {})", id, imageDir);
                try {
                    boolean deleteSuccess = fileStorageService.deleteImages(imageDir);
                    if (!deleteSuccess) {
                        logger.warn("L'eliminazione delle immagini per la proprietà {} è fallita, ma si procederà con l'eliminazione dal DB.", id);
                    } else {
                        logger.info("Immagini eliminate con successo per la proprietà {} (ULID: {})", id, imageDir);
                    }
                } catch (Exception e) {
                    // Non blocchiamo l'eliminazione dal DB se lo storage fallisce; logghiamo l'errore
                    logger.warn("Errore durante l'eliminazione delle immagini per la proprietà {} (ULID: {}): {}", id, imageDir, e.getMessage(), e);
                }
            }

            propertyRepository.deleteById(id);
            logger.info("Proprietà con ID {} eliminata con successo.", id);
        } else {
            logger.warn("Tentativo di eliminare una proprietà non esistente con ID: {}", id);
            throw new EntityNotFoundException("Proprietà con ID " + id + " non trovata.");
        }
    }

    /**
     * Recupera la cronologia degli immobili in base agli ID specificati.
     * Metodo placeholder - la logica sarà implementata successivamente.
     *
     * @param request DTO contenente la lista degli ID immobili da recuperare
     * @return lista vuota (implementazione temporanea)
     */
    public List<PropertyResponse> getPropertyHistory(PropertyHistoryRequest request) {
        logger.debug("Metodo getPropertyHistory chiamato con request: {}", request);
        Objects.requireNonNull(request, "request must not be null");
        List<String> propertyIds = request.getPropertyIds();
        Objects.requireNonNull(propertyIds, "propertyIds must not be null");

        if (propertyIds.isEmpty()) {
            return List.of();
        }

        // Recupera le entità Property tramite il PropertyQueryService
        List<Property> properties = propertyQueryService.getPropertiesByIds(propertyIds);

        // Mappa ogni Property in PropertyResponse usando il ResponseMapperRegistry
        return properties.stream()
                .map(responseMapperRegistry::map)
                .toList();
    }
}
