package com.dieti.dietiestatesbackend.exception;

import java.util.Map;

/**
 * Eccezione usata per trasportare errori di validazione semantica.
 * Contiene una mappa campo -> messaggio per fornire feedback dettagliato al client.
 */
public class InvalidPayloadException extends RuntimeException {

    private final Map<String, String> errors;

    public InvalidPayloadException(Map<String, String> errors) {
        super("Invalid payload");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}