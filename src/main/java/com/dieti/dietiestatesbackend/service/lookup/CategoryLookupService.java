package com.dieti.dietiestatesbackend.service.lookup;

import com.dieti.dietiestatesbackend.entities.PropertyCategory;
import java.util.Optional;

/**
 * Service interface per il lookup delle PropertyCategory.
 */
public interface CategoryLookupService {
    Optional<PropertyCategory> findByNameOrSubcategory(String name);
}