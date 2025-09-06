package com.dieti.dietiestatesbackend.service.geocoding;

import java.util.Optional;
import com.dieti.dietiestatesbackend.entities.Address;
import com.dieti.dietiestatesbackend.entities.Coordinates; // Importa l'entità Coordinates
import com.dieti.dietiestatesbackend.exception.GeocodingException;

/**
 * Interfaccia per servizi di geocoding.
 */
public interface GeocodingService {

    /**
     * Risolve le coordinate geografiche per un indirizzo.
     *
     * @param address indirizzo da geocodificare
     * @return Optional con le coordinate se trovate, altrimenti empty
     * @throws GeocodingException in caso di errori nel servizio di geocoding
     */
    Optional<Coordinates> geocode(Address address) throws GeocodingException;
}