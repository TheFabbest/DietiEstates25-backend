package com.dieti.dietiestatesbackend.service;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.dieti.dietiestatesbackend.dto.request.CreateResidentialPropertyRequest;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.ResidentialProperty;
import com.dieti.dietiestatesbackend.entities.Contract;
import com.dieti.dietiestatesbackend.entities.PropertyCategory;
import com.dieti.dietiestatesbackend.entities.Address;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.exception.EntityNotFoundException;
import com.dieti.dietiestatesbackend.mappers.PropertyMapper;
import com.dieti.dietiestatesbackend.enums.PropertyType;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

/**
 * Creator per proprietà residenziali.
 * Sposta qui la logica specifica precedentemente presente in PropertyService.
 */
@Component
public class ResidentialPropertyCreator implements PropertyCreator<CreateResidentialPropertyRequest> {

    @Override
    public Property create(CreateResidentialPropertyRequest r, User agent, Contract contract, PropertyCategory category, Address address, EntityManager entityManager, Validator validator) {

        // Validazione bean
        Set<ConstraintViolation<CreateResidentialPropertyRequest>> violations = validator.validate(r);
        if (!violations.isEmpty()) {
            String msg = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(java.util.stream.Collectors.joining("; "));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validation failed: " + msg);
        }

        ResidentialProperty rp = PropertyMapper.toResidentialEntity(r);

        // Risoluzione heating se fornito
        if (r.getHeatingType() != null && !r.getHeatingType().isBlank()) {
            List<com.dieti.dietiestatesbackend.entities.Heating> hs = entityManager.createQuery(
                    "SELECT h FROM Heating h WHERE h.name = :name", com.dieti.dietiestatesbackend.entities.Heating.class)
                    .setParameter("name", r.getHeatingType())
                    .getResultList();
            if (hs.isEmpty()) {
                throw new EntityNotFoundException("Heating not found with name: " + r.getHeatingType());
            }
            rp.setHeating(hs.get(0));
        }

        // Le associazioni comuni (agent, contract, category, address, createdAt) sono impostate dal PropertyService
        return rp;
    }

    @Override
    public PropertyType supports() {
        return PropertyType.RESIDENTIAL;
    }
}