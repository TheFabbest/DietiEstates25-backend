package com.dieti.dietiestatesbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dieti.dietiestatesbackend.entities.Offer;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
}