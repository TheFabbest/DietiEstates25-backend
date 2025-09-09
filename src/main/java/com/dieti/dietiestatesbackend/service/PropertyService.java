package com.dieti.dietiestatesbackend.service;
 
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import com.dieti.dietiestatesbackend.dto.request.CreatePropertyRequest;
import com.dieti.dietiestatesbackend.dto.request.FilterRequest;
import com.dieti.dietiestatesbackend.dto.response.PropertyResponse;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.service.geocoding.Coordinates;
import com.dieti.dietiestatesbackend.service.places.PlacesService;
import com.dieti.dietiestatesbackend.service.places.dto.PlaceDTO;
 
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
    private static final int DEFAULT_LEGACY_PAGE_SIZE = 50;

    private final PropertyQueryServiceInterface propertyQueryService;
    private final PropertyManagementService propertyManagementService;
    private final PlacesService placesService;
 
    /**
     * Costruttore principale: tutte le dipendenze sono richieste.
     */
    @Autowired
    public PropertyService(PropertyQueryServiceInterface propertyQueryService,
                           PropertyManagementService propertyManagementService,
                           PlacesService placesService) {
        this.propertyQueryService = Objects.requireNonNull(propertyQueryService, "propertyQueryService");
        this.propertyManagementService = Objects.requireNonNull(propertyManagementService, "propertyManagementService");
        this.placesService = Objects.requireNonNull(placesService, "placesService");
    }

    // --- Read (delegated) ---
    /**
     * Legacy-friendly search returning a List. Internally delegates to
     * {@link PropertyQueryService#searchProperties(String, org.springframework.data.domain.Pageable)}
     * using a sensible default page size to avoid large results.
     */
    public List<Property> searchProperties(String keyword) {
        return propertyQueryService.searchProperties(normalize(keyword),
                PageRequest.of(0, DEFAULT_LEGACY_PAGE_SIZE)).getContent();
    }

    /**
     * Search with filters. Delegates to PropertyQueryService which executes
     * a Specification-based query with fetch joins optimized for reads.
     * Geographic filters (centerLatitude, centerLongitude, radiusInMeters) are now mandatory.
     */
    public List<Property> searchPropertiesWithFilters(FilterRequest filters) {
        Objects.requireNonNull(filters, "filters must not be null");
        return propertyQueryService.searchPropertiesWithFilters(filters);
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

    // --- Helper methods (extracted) ---
    private static String normalize(String keyword) {
        return keyword == null ? "" : keyword.trim();
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
}
