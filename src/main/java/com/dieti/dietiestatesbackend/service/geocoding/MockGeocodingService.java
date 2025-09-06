package com.dieti.dietiestatesbackend.service.geocoding;

import java.util.Optional;
import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.dieti.dietiestatesbackend.entities.Address;
import com.dieti.dietiestatesbackend.entities.Coordinates; // Importa l'entità Coordinates
import com.dieti.dietiestatesbackend.exception.GeocodingException;

/**
 * Implementazione mock del servizio di geocoding utilizzata per sviluppo e test.
 * Restituisce coordinate fisse per un indirizzo di esempio ("Via Roma 1, Milano"),
 * altrimenti Optional.empty().
 */
@Service
public class MockGeocodingService implements GeocodingService {

    @Override
    public Optional<Coordinates> geocode(Address address) throws GeocodingException {
        if (address == null) {
            throw new IllegalArgumentException("address must not be null");
        }

        // Restituisce sempre coordinate fisse per qualsiasi indirizzo (mock per sviluppo)
        // Coordinate di esempio (Piazza del Duomo approssimativo a Milano)
        return Optional.of(new Coordinates(BigDecimal.valueOf(45.4642035), BigDecimal.valueOf(9.189982)));
    }
}