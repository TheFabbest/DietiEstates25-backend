package com.dieti.dietiestatesbackend.service.lookup;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dieti.dietiestatesbackend.entities.PropertyCategory;
import jakarta.persistence.EntityManager;

/**
 * Implementazione di CategoryLookupService che usa EntityManager per cercare
 * per category o subcategory (mantiene comportamento precedente).
 */
@Service
public class CategoryLookupServiceImpl implements CategoryLookupService {

    private final EntityManager entityManager;

    @Autowired
    public CategoryLookupServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<PropertyCategory> findByNameOrSubcategory(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        List<PropertyCategory> cats = entityManager.createQuery(
                "SELECT pc FROM PropertyCategory pc WHERE pc.category = :name OR pc.subcategory = :name",
                PropertyCategory.class)
                .setParameter("name", name)
                .getResultList();
        if (cats.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(cats.get(0));
    }
}