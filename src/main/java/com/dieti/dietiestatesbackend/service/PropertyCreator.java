package com.dieti.dietiestatesbackend.service;

import com.dieti.dietiestatesbackend.dto.request.CreatePropertyRequest;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.entities.Contract;
import com.dieti.dietiestatesbackend.entities.PropertyCategory;
import com.dieti.dietiestatesbackend.entities.Address;

import jakarta.persistence.EntityManager;
import jakarta.validation.Validator;

import com.dieti.dietiestatesbackend.enums.PropertyType;

/**
 * Interfaccia generica per la creazione di entità Property specifiche per tipo.
 * Il parametro type-safe T permette alle implementazioni concrete di ricevere
 * direttamente il DTO specifico (es. CreateResidentialPropertyRequest) evitando
 * controlli runtime con instanceof e cast espliciti.
 */
public interface PropertyCreator<T extends CreatePropertyRequest> {
    Property create(T request,
                    User agent,
                    Contract contract,
                    PropertyCategory category,
                    Address address,
                    EntityManager entityManager,
                    Validator validator);

    /**
     * Indica quale PropertyType è gestito da questo creator.
     */
    PropertyType supports();
}