package com.dieti.dietiestatesbackend.service.lookup;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import com.dieti.dietiestatesbackend.entities.PropertyCategory;
import jakarta.persistence.EntityManager;

/**
 * Implementazione di CategoryLookupService che usa EntityManager.
 * Updated to support the new canonical naming approach.
 */
@Service
public class CategoryLookupServiceImpl implements CategoryLookupService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryLookupServiceImpl.class);

    private final EntityManager entityManager;

    @Autowired
    public CategoryLookupServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Cacheable(value = "categories", key = "#name")
    public Optional<PropertyCategory> findByName(String name) {
        logger.debug("Attempting to find category by name: {} (from DB, not cache)", name);
        if (name == null || name.isBlank()) {
            logger.debug("Category name is null or blank.");
            return Optional.empty();
        }
        List<PropertyCategory> categories = entityManager.createQuery(
                "SELECT pc FROM PropertyCategory pc WHERE pc.name = :name",
                PropertyCategory.class)
                .setParameter("name", name)
                .getResultList();
        if (categories.isEmpty()) {
            logger.debug("No category found for name: {}", name);
            return Optional.empty();
        }
        logger.debug("Category found for name: {}", name);
        return Optional.of(categories.get(0));
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
}