package com.dieti.dietiestatesbackend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dieti.dietiestatesbackend.entities.Property;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    // Method to get property with all relationships loaded
    @Query("SELECT p FROM Property p " +
           "LEFT JOIN FETCH p.contract " +
           "LEFT JOIN FETCH p.propertyCategory " +
           "LEFT JOIN FETCH p.agent " +
           "LEFT JOIN FETCH p.address " +
           "WHERE p.id = :id")
    Optional<Property> findByIdWithDetails(@Param("id") Long id);
}