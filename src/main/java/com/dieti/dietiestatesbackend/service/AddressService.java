package com.dieti.dietiestatesbackend.service;

import java.sql.SQLException;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dieti.dietiestatesbackend.entities.Address;
import com.dieti.dietiestatesbackend.repositories.AddressRepository;

@Service
public class AddressService {
    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    private final AddressRepository addressRepository;

    @Autowired
    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Address getAddress(Long id) throws SQLException {
        return addressRepository.getReferenceById(id);
    }
}