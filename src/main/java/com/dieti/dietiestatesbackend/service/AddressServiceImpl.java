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
import com.dieti.dietiestatesbackend.service.geocoding.GeocodingService;
import com.dieti.dietiestatesbackend.entities.Coordinates; // Importa l'entità Coordinates
import com.dieti.dietiestatesbackend.exception.GeocodingException;
import org.springframework.http.HttpStatus;

/**
 * Implementazione concreta del contratto {@link AddressService}.
 * Incapsula la logica di creazione e recupero degli indirizzi.
 */
@Service
@Transactional
public class AddressServiceImpl implements AddressService {
    private static final Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);
    private final AddressRepository addressRepository;
    private final GeocodingService geocodingService;
 
    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository, GeocodingService geocodingService) {
        this.addressRepository = addressRepository;
        this.geocodingService = geocodingService;
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
        adr.setStreetNumber(request.getStreetNumber());
        adr.setBuilding(request.getBuilding());
        if (request.getLatitude() != null && request.getLongitude() != null) {
            Coordinates coords = new Coordinates();
            coords.setLatitude(request.getLatitude());
            coords.setLongitude(request.getLongitude());
            adr.setCoordinates(coords);
        } else {   
            Optional<Coordinates> opt = geocodingService.geocode(adr);
            if (opt.isEmpty()) {
                throw new GeocodingException("Impossibile ottenere le coordinate per l'indirizzo fornito.", HttpStatus.NOT_FOUND);
            }
            adr.setCoordinates(opt.get());
        }
        adr.setCreatedAt(LocalDateTime.now());
        Address saved = addressRepository.save(adr);
        logger.debug("Indirizzo creato con id={}", saved.getId());
        return saved;
    }

    @Override
    public Address geocodeAddress(Address address) {
        if (address == null) {
            logger.debug("geocodeAddress chiamato con address nullo");
            throw new IllegalArgumentException("Address cannot be null");
        }
        
        // Effettua il geocoding dell'indirizzo
        Optional<Coordinates> opt = geocodingService.geocode(address);
        if (opt.isEmpty()) {
            throw new GeocodingException("Impossibile ottenere le coordinate per l'indirizzo fornito.", HttpStatus.NOT_FOUND);
        }
        address.setCoordinates(opt.get());
        
        logger.debug("Indirizzo geocodificato con successo");
        return address;
    }
}