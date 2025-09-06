package com.dieti.dietiestatesbackend.service;
 
import com.dieti.dietiestatesbackend.dto.request.AddressRequest;
import com.dieti.dietiestatesbackend.entities.Address;
import java.util.Optional;
 
/**
 * Contratto per le operazioni sugli indirizzi.
 * Implementazioni concrete (es. {@code AddressServiceImpl}) forniranno la logica effettiva.
 */
public interface AddressService {
    Optional<Address> findById(Long id);
    Address createFromRequest(AddressRequest request);
    Address geocodeAddress(Address address);
}