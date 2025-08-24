package com.dieti.dietiestatesbackend.service;

import org.springframework.stereotype.Component;

import com.dieti.dietiestatesbackend.dto.request.CreateGaragePropertyRequest;
import com.dieti.dietiestatesbackend.entities.Garage;
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
 * Creator per autorimesse/garage.
 */
@Component
public class GaragePropertyCreator implements PropertyCreator<CreateGaragePropertyRequest> {

    @Override
    public Property create(CreateGaragePropertyRequest r, User agent, Contract contract, PropertyCategory category, Address address, EntityManager entityManager, Validator validator) {

        Garage garage = PropertyMapper.toGarageEntity(r);
        if (r.getNumeroPiani() != null) {
            garage.setNumberOfFloors(r.getNumeroPiani());
        }
        garage.setHasSurveillance(r.isHaSorveglianza());
        return garage;
    }

    @Override
    public PropertyType supports() {
        return PropertyType.GARAGE;
    }
}