package com.dieti.dietiestatesbackend.repositories;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dieti.dietiestatesbackend.entities.Property;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {
    // searches for properties with a given keyword in the "description"
    // Rimossa la query custom, Spring Data JPA genererà automaticamente la query
    public List<Property> findByDescriptionContainingIgnoreCase(@Param("keyword") String keyword);

    // gets featured properties - ordinate per data di creazione decrescente e limitate a 4
    @Query("SELECT p FROM Property p ORDER BY p.createdAt DESC")
    public Page<Property> getFeatured(Pageable pageable);

}