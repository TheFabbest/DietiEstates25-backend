package com.dieti.dietiestatesbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dieti.dietiestatesbackend.entities.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

}