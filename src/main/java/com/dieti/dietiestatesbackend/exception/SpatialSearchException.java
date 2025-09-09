package com.dieti.dietiestatesbackend.exception;

import org.springframework.http.HttpStatus;

/**
 * Eccezione per errori relativi alla ricerca spaziale e geografica.
 * Utilizzata per gestire errori di validazione dei parametri geografici,
 * coordinate non valide e altri problemi legati alla ricerca spaziale.
 *
 * Segue il principio di Single Responsibility essendo dedicata esclusivamente
 * agli errori di ricerca spaziale.
 */
public class SpatialSearchException extends RuntimeException {

    private final HttpStatus httpStatus;

    /**
     * Crea una nuova eccezione con messaggio e status HTTP.
     *
     * @param message il messaggio di errore descrittivo
     * @param httpStatus lo status HTTP da restituire
     */
    public SpatialSearchException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    /**
     * Crea una nuova eccezione con messaggio, causa e status HTTP.
     *
     * @param message il messaggio di errore descrittivo
     * @param cause l'eccezione originale che ha causato questo errore
     * @param httpStatus lo status HTTP da restituire
     */
    public SpatialSearchException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    /**
     * Restituisce lo status HTTP associato a questa eccezione.
     *
     * @return lo status HTTP
     */
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}