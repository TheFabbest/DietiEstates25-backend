package com.dieti.dietiestatesbackend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dieti.dietiestatesbackend.entities.PropertyCategory;

@Repository
public interface PropertyCategoryRepository extends JpaRepository<PropertyCategory, Long> {
    Optional<PropertyCategory> findByName(String name);
    boolean existsByName(String name);
}