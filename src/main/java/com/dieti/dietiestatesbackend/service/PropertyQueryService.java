package com.dieti.dietiestatesbackend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dieti.dietiestatesbackend.dto.request.FilterRequest;
import com.dieti.dietiestatesbackend.entities.Coordinates;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.exception.EntityNotFoundException;
import com.dieti.dietiestatesbackend.exception.SpatialSearchException;
import com.dieti.dietiestatesbackend.repositories.PropertyRepository;
import com.dieti.dietiestatesbackend.specification.PropertySpecifications;
import com.dieti.dietiestatesbackend.util.HaversineUtils;

/**
 * Service dedicated to read-only queries for Property.
 * Responsibility: expose read methods (search, filters, featured, detail).
 * Pagination is supported where appropriate; helper paginate() keeps logic testable.
 * Implementa l'interfaccia PropertyQueryServiceInterface per seguire il principio di Dependency Inversion.
 */
@Service
public class PropertyQueryService implements PropertyQueryServiceInterface {

    private final PropertyRepository propertyRepository;

    public PropertyQueryService(PropertyRepository propertyRepository) {
        this.propertyRepository = Objects.requireNonNull(propertyRepository, "propertyRepository");
    }

    /**
     * Search properties by keyword with pagination.
     * The repository returns a list with fetch-joins; this method paginates the list.
     */
    public Page<Property> searchProperties(String keyword, Pageable pageable) {
        Objects.requireNonNull(pageable, "pageable must not be null");
        List<Property> results = propertyRepository.searchByDescription(normalize(keyword));
        return paginate(results, pageable);
    }

    /**
     * Search using rich filters. Returns a full list because the specification-based query
     * already applies fetch-joins optimized for read.
     * Applies Haversine distance refinement after the database query for precise geographic filtering.
     * Geographic filters (centerLatitude, centerLongitude, radiusInMeters) are now mandatory and validated by Bean Validation.
     */
    public List<Property> searchPropertiesWithFilters(FilterRequest filters) {
        Objects.requireNonNull(filters, "filters must not be null");
        
        // First get results using specification-based query (includes approximate bounding box filter)
        List<Property> results = propertyRepository.findAll(PropertySpecifications.withFilters(filters));
        
        // Apply precise Haversine distance refinement (geographic filters are now always present)
        results = refineWithHaversineDistance(results, filters);
        
        return results;
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

    // --- Helpers ---

    private static String normalize(String keyword) {
        return keyword == null ? "" : keyword.trim();
    }

    private Page<Property> paginate(List<Property> items, Pageable pageable) {
        if (items == null || items.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }
        int total = items.size();
        int start = Math.min((int) pageable.getOffset(), total);
        int end = Math.min(start + pageable.getPageSize(), total);
        List<Property> content = items.subList(start, end);
        return new PageImpl<>(content, pageable, total);
    }
    
    // --- Geographic filtering helpers ---
    
    /**
     * Validates coordinate ranges (now called directly from refineWithHaversineDistance)
     */
    private void validateCoordinates(BigDecimal latitude, BigDecimal longitude) {
        try {
            Coordinates.validateCoordinates(latitude, longitude);
        } catch (IllegalArgumentException e) {
            throw new SpatialSearchException(
                "Coordinate geografiche non valide: " + e.getMessage(),
                org.springframework.http.HttpStatus.BAD_REQUEST
            );
        }
    }
    
    /**
     * Refines the property list using precise Haversine distance calculation
     * Filters out properties that are outside the specified radius from the center point
     * Geographic filters are now mandatory and validated by Bean Validation
     */
    private List<Property> refineWithHaversineDistance(List<Property> properties, FilterRequest filters) {
        BigDecimal centerLat = filters.getCenterLatitude();
        BigDecimal centerLon = filters.getCenterLongitude();
        double radiusMeters = filters.getRadiusInMeters();
        
        // Validate coordinates (Bean Validation ensures they are not null)
        validateCoordinates(centerLat, centerLon);
        
        if (radiusMeters < 0) {
            throw new SpatialSearchException(
                "Il raggio di ricerca deve essere maggiore o uguale a 0",
                org.springframework.http.HttpStatus.BAD_REQUEST
            );
        }
        
        return properties.stream()
                .filter(property -> {
                    if (property.getAddress() == null || property.getAddress().getCoordinates() == null) {
                        return false; // Skip properties without coordinates
                    }
                    
                    BigDecimal propertyLat = property.getAddress().getCoordinates().getLatitude();
                    BigDecimal propertyLon = property.getAddress().getCoordinates().getLongitude();
                    
                    double distance = HaversineUtils.calculateDistance(centerLat, centerLon, propertyLat, propertyLon);
                    return distance <= radiusMeters;
                })
                .collect(Collectors.toList());
    }
}