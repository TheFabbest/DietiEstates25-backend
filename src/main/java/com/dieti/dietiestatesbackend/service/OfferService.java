package com.dieti.dietiestatesbackend.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dieti.dietiestatesbackend.entities.Offer;
import com.dieti.dietiestatesbackend.repositories.OfferRepository;

@Service
public class OfferService {
    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    private final OfferRepository offerRepository;

    @Autowired
    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    public Offer getOffer(Long id) throws SQLException {
        return offerRepository.getReferenceById(id);
    }
}