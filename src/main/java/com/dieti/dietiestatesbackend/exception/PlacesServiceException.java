package com.dieti.dietiestatesbackend.exception;

/**
 * Eccezione personalizzata per gestire errori relativi al servizio Places.
 * Incapsula tutti gli errori relativi all'interazione con servizi esterni di geolocalizzazione.
 */
public class PlacesServiceException extends RuntimeException {

    /**
     * Crea una nuova eccezione con un messaggio di errore.
     *
     * @param message il messaggio di errore descrittivo
     */
    public PlacesServiceException(String message) {
        super(message);
    }

    /**
     * Crea una nuova eccezione con un messaggio di errore e una causa.
     *
     * @param message il messaggio di errore descrittivo
     * @param cause l'eccezione originale che ha causato questo errore
     */
    public PlacesServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Crea una nuova eccezione con una causa.
     *
     * @param cause l'eccezione originale che ha causato questo errore
     */
    public PlacesServiceException(Throwable cause) {
        super(cause);
    }
}