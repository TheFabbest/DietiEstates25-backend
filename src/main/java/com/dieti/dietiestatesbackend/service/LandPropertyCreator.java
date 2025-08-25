package com.dieti.dietiestatesbackend.service;

import org.springframework.stereotype.Component;

import com.dieti.dietiestatesbackend.dto.request.CreateLandPropertyRequest;
import com.dieti.dietiestatesbackend.dto.request.CreatePropertyRequest;
import com.dieti.dietiestatesbackend.entities.Land;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.Contract;
import com.dieti.dietiestatesbackend.entities.PropertyCategory;
import com.dieti.dietiestatesbackend.entities.Address;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.mappers.PropertyMapper;
import com.dieti.dietiestatesbackend.enums.PropertyType;

import jakarta.persistence.EntityManager;
import jakarta.validation.Validator;

/**
 * Creator per terreni.
 */
@Component
public class LandPropertyCreator implements PropertyCreator<CreateLandPropertyRequest> {

    @Override
    public Property create(CreateLandPropertyRequest r, User agent, Contract contract, PropertyCategory category, Address address, EntityManager entityManager, Validator validator) {

        Land land = PropertyMapper.toLandEntity((CreatePropertyRequest) r);
        if (r.getHaIngressoDallaStrada() != null) {
            land.setAccessibleFromStreet(r.getHaIngressoDallaStrada());
        }
        return land;
    }

    @Override
    public PropertyType supports() {
        return PropertyType.LAND;
    }
}