package com.dieti.dietiestatesbackend.service.lookup;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dieti.dietiestatesbackend.entities.PropertyCategory;
import jakarta.persistence.EntityManager;

/**
 * Implementazione di CategoryLookupService che usa EntityManager.
 * Updated to support the new canonical naming approach.
 */
@Service
public class CategoryLookupServiceImpl implements CategoryLookupService {

    private final EntityManager entityManager;

    @Autowired
    public CategoryLookupServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<PropertyCategory> findByName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        List<PropertyCategory> categories = entityManager.createQuery(
                "SELECT pc FROM PropertyCategory pc WHERE pc.name = :name",
                PropertyCategory.class)
                .setParameter("name", name)
                .getResultList();
        return categories.isEmpty() ? Optional.empty() : Optional.of(categories.get(0));
    }

    @Override
    public List<PropertyCategory> findByPropertyType(String propertyType) {
        if (propertyType == null || propertyType.isBlank()) {
            return List.of();
        }
        return entityManager.createQuery(
                "SELECT pc FROM PropertyCategory pc WHERE pc.propertyType = :propertyType AND pc.isActive = true",
                PropertyCategory.class)
                .setParameter("propertyType", propertyType)
                .getResultList();
    }

    @Override
    public List<String> findDistinctActivePropertyTypes() {
        return entityManager.createQuery(
                "SELECT DISTINCT pc.propertyType FROM PropertyCategory pc WHERE pc.isActive = true",
                String.class)
                .getResultList();
    }
    
    @Override
    @Deprecated
    public Optional<PropertyCategory> findByNameOrSubcategory(String name) {
        // Legacy method - delegate to findByName for backward compatibility
        return findByName(name);
    }
}