package com.dieti.dietiestatesbackend.service;

import java.sql.SQLException;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dieti.dietiestatesbackend.entities.Address;
import com.dieti.dietiestatesbackend.repositories.AddressRepository;

@Service
@Transactional
public class AddressService {
    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    private final AddressRepository addressRepository;

    @Autowired
    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Address getAddress(Long id) throws SQLException {
        return addressRepository.findById(id).get();
    }
}