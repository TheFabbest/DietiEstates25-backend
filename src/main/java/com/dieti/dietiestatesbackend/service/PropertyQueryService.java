package com.dieti.dietiestatesbackend.service;

import com.dieti.dietiestatesbackend.util.BoundingBoxUtility;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
 
import com.dieti.dietiestatesbackend.dto.request.FilterRequest;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.exception.EntityNotFoundException;
import com.dieti.dietiestatesbackend.repositories.PropertyRepository;

/**
 * Service dedicated to read-only queries for Property.
 * Responsibility: expose read methods (search, filters, featured, detail).
 * Pagination is supported where appropriate and handled at database level for optimal performance.
 * Implementa l'interfaccia PropertyQueryServiceInterface per seguire il principio di Dependency Inversion.
 */
@Service
public class PropertyQueryService implements PropertyQueryServiceInterface {

    private final PropertyRepository propertyRepository;
    private final BoundingBoxUtility boundingBoxUtility;
    private static final Logger logger = LoggerFactory.getLogger(PropertyQueryService.class);
 
    public PropertyQueryService(PropertyRepository propertyRepository, BoundingBoxUtility boundingBoxUtility) {
        this.propertyRepository = Objects.requireNonNull(propertyRepository, "propertyRepository");
        this.boundingBoxUtility = Objects.requireNonNull(boundingBoxUtility, "boundingBoxUtility");
    }


    /**
     * Search using rich filters with pagination. Uses specification-based query
     * with fetch-joins optimized for read and precise geographic filtering.
     * Geographic filters (centerLatitude, centerLongitude, radiusInMeters) are now mandatory and validated by Bean Validation.
     * All geographic filtering is now handled at the database level through JPA Specifications.
     */
    public Page<Property> searchPropertiesWithFilters(FilterRequest filters, Pageable pageable) {
        Objects.requireNonNull(filters, "filters must not be null");
        Objects.requireNonNull(pageable, "pageable must not be null");
        
        // Calculate bounding box for geographic filtering using utility class
        double[] bounds = boundingBoxUtility.calculateBoundingBoxAsDouble(
            filters.getCenterLatitude(),
            filters.getCenterLongitude(),
            filters.getRadiusInMeters()
        );
        
        // Use custom query with eager loading and geographic filtering
        return propertyRepository.searchWithFiltersAndEagerFetch(
            bounds[0], bounds[1], bounds[2], bounds[3], pageable
        );
    }

    /**
     * Get featured properties (latest 4).
     */
    public List<Property> getFeatured() {
        return propertyRepository.getFeatured(PageRequest.of(0, 4)).getContent();
    }

    /**
     * Get detailed property by id (throws if not found).
     */
    public Property getProperty(long propertyID) {
        return propertyRepository.findDetailedById(propertyID)
                .orElseThrow(() -> new EntityNotFoundException("Property not found with id: " + propertyID));
    }

    /**
     * Ottiene una lista di proprietà in base agli ID specificati.
     * Restituisce solo le proprietà esistenti, ignorando gli ID non trovati.
     *
     * @param propertyIds lista degli ID delle proprietà da recuperare
     * @return lista delle proprietà trovate
     */
    public List<Property> getPropertiesByIds(List<String> propertyIds) {
        Objects.requireNonNull(propertyIds, "propertyIds must not be null");
        
        if (propertyIds.isEmpty()) {
            return List.of();
        }
        
        // Converti gli ID da String a Long, ignorando gli ID non validi
        List<Long> ids = propertyIds.stream()
                .map(idStr -> {
                    try {
                        return Long.valueOf(idStr);
                    } catch (NumberFormatException e) {
                        logger.warn("Invalid property id '{}' - ignoring", idStr);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
        
        return propertyRepository.findAllDetailedByIdIn(ids);
    }
}