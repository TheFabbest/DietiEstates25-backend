package com.dieti.dietiestatesbackend.service;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dieti.dietiestatesbackend.dto.request.FilterRequest;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.repositories.PropertyRepository;
import com.dieti.dietiestatesbackend.specification.PropertySpecifications;

@Service
@Transactional
public class PropertyService {
    
    private static final Logger logger = Logger.getLogger(PropertyService.class.getName());
    private final PropertyRepository propertyRepository;

    @Autowired
    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    // // Common operations
    public List<Property> searchProperties(String keyword) throws SQLException {
        return propertyRepository.findByDescriptionContainingIgnoreCase(keyword);
    }

        public List<Property> searchPropertiesWithFilters(String keyword, FilterRequest filters) throws SQLException {
        return propertyRepository.findAll(PropertySpecifications.withFilters(keyword, filters));
    }

    // More specific methods
    public List<Property> getFeatured() throws SQLException {
        return propertyRepository.getFeatured();
    }

    public Property getProperty(long propertyID) {
        return propertyRepository.findById(propertyID).get();
    }
}