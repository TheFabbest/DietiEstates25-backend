package com.dieti.dietiestatesbackend.exception;

import org.springframework.http.HttpStatus;

/**
 * Eccezione lanciata da servizi di geocoding in caso di errori.
 * Scelta: unchecked (estende RuntimeException) per coerenza con altre eccezioni del progetto.
 * Estesa per includere HttpStatus per una gestione più granulare degli errori.
 */
public class GeocodingException extends RuntimeException {
    private final HttpStatus httpStatus;

    public GeocodingException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public GeocodingException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public GeocodingException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public GeocodingException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}