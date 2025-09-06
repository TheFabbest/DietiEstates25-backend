package com.dieti.dietiestatesbackend.service.lookup;

import com.dieti.dietiestatesbackend.entities.PropertyCategory;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;

/**
 * Service interface per il lookup delle PropertyCategory.
 * Updated to support the new canonical naming approach.
 */
public interface CategoryLookupService {
    /**
     * Find a PropertyCategory by its specific name (e.g., "Apartment", "Villa", "Office").
     * This is the primary lookup method according to the new architecture.
     */
    @Cacheable
    Optional<PropertyCategory> findByName(String name);
    
    /**
     * Find all PropertyCategories that belong to a specific property type.
     * Used for the cascade dropdown implementation.
     */
    List<PropertyCategory> findByPropertyType(String propertyType);

    /**
     * Retrieve distinct canonical property types (e.g. RESIDENTIAL, COMMERCIAL, LAND, GARAGE)
     * from active categories with a single optimized query.
     */
    List<String> findDistinctActivePropertyTypes();
    
    /**
     * Legacy method for backward compatibility during transition.
     * @deprecated Use findByName instead
     */
    @Deprecated
    Optional<PropertyCategory> findByNameOrSubcategory(String name);
}