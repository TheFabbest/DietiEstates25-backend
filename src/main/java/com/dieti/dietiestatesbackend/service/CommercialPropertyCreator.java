package com.dieti.dietiestatesbackend.service;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.dieti.dietiestatesbackend.dto.request.CreateCommercialPropertyRequest;
import com.dieti.dietiestatesbackend.entities.CommercialProperty;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.Contract;
import com.dieti.dietiestatesbackend.entities.PropertyCategory;
import com.dieti.dietiestatesbackend.entities.Address;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.mappers.PropertyMapper;
import com.dieti.dietiestatesbackend.enums.PropertyType;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

/**
 * Creator per proprietà commerciali.
 * Incapsula la validazione e la costruzione dell'entità specifica.
 */
@Component
public class CommercialPropertyCreator implements PropertyCreator<CreateCommercialPropertyRequest> {

    @Override
    public Property create(CreateCommercialPropertyRequest r, User agent, Contract contract, PropertyCategory category, Address address, EntityManager entityManager, Validator validator) {

        // Validazione bean
        Set<ConstraintViolation<CreateCommercialPropertyRequest>> violations = validator.validate(r);
        if (!violations.isEmpty()) {
            String msg = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(java.util.stream.Collectors.joining("; "));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validation failed: " + msg);
        }

        CommercialProperty cp = PropertyMapper.toCommercialEntity(r);
        return cp;
    }

    @Override
    public PropertyType supports() {
        return PropertyType.COMMERCIAL;
    }
}