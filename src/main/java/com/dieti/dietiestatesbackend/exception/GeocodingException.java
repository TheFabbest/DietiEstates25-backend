package com.dieti.dietiestatesbackend.exception;

/**
 * Eccezione lanciata da servizi di geocoding in caso di errori.
 * Scelta: unchecked (estende RuntimeException) per coerenza con altre eccezioni del progetto.
 */
public class GeocodingException extends RuntimeException {
    public GeocodingException(String message) {
        super(message);
    }

    public GeocodingException(String message, Throwable cause) {
        super(message, cause);
    }
}