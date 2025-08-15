package com.dieti.dietiestatesbackend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dieti.dietiestatesbackend.entities.Property;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    // searches for properties with a given keyword in the "description"
    @Query("SELECT p FROM Property p WHERE LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    public List<Property> findByDescriptionContainingIgnoreCase(@Param("keyword") String keyword);

    // gets featured properties
    @Query("SELECT p FROM Property p WHERE p.id < 4")
    public List<Property> getFeatured();

}