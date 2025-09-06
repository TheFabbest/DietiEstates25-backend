package com.dieti.dietiestatesbackend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dieti.dietiestatesbackend.entities.Heating;

@Repository
public interface HeatingRepository extends JpaRepository<Heating, Long> {
    Optional<Heating> findByName(String name);
    boolean existsByName(String name);
}