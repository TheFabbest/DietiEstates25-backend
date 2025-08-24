package com.dieti.dietiestatesbackend.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dieti.dietiestatesbackend.dto.request.AddressRequest;
import com.dieti.dietiestatesbackend.entities.Address;
import com.dieti.dietiestatesbackend.repositories.AddressRepository;

/**
 * Implementazione concreta del contratto {@link AddressService}.
 * Incapsula la logica di creazione e recupero degli indirizzi.
 */
@Service
@Transactional
public class AddressServiceImpl implements AddressService {
    private static final Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);
    private final AddressRepository addressRepository;

    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public Optional<Address> findById(Long id) {
        if (id == null) {
            logger.debug("findById chiamato con id nullo");
            return Optional.empty();
        }
        return addressRepository.findById(id);
    }

    @Override
    public Address createFromRequest(AddressRequest request) {
        if (request == null) {
            logger.debug("createFromRequest chiamato con request nulla");
            throw new IllegalArgumentException("AddressRequest cannot be null");
        }
        Address adr = new Address();
        adr.setCountry(request.getCountry());
        adr.setProvince(request.getProvince());
        adr.setCity(request.getCity());
        adr.setStreet(request.getStreet());
        adr.setStreet_number(request.getStreetNumber());
        adr.setBuilding(request.getBuilding());
        adr.setLatitude(request.getLatitude());
        adr.setLongitude(request.getLongitude());
        adr.setCreatedAt(LocalDateTime.now());
        Address saved = addressRepository.save(adr);
        logger.debug("Indirizzo creato con id={}", saved.getId());
        return saved;
    }
}