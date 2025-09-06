package com.dieti.dietiestatesbackend.service;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dieti.dietiestatesbackend.dto.request.FilterRequest;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.exception.EntityNotFoundException;
import com.dieti.dietiestatesbackend.repositories.PropertyRepository;
import com.dieti.dietiestatesbackend.specification.PropertySpecifications;

/**
 * Service dedicated to read-only queries for Property.
 * Responsibility: expose read methods (search, filters, featured, detail).
 * Pagination is supported where appropriate; helper paginate() keeps logic testable.
 */
@Service
public class PropertyQueryService {

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
     */
    public List<Property> searchPropertiesWithFilters(String keyword, FilterRequest filters) {
        Objects.requireNonNull(filters, "filters must not be null");
        return propertyRepository.findAll(PropertySpecifications.withFilters(normalize(keyword), filters));
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
}