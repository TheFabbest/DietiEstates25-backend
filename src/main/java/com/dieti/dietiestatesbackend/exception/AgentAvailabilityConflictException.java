package com.dieti.dietiestatesbackend.exception;

import java.util.Collections;
import java.util.Map;

/**
 * Eccezione lanciata quando uno slot di disponibilità è in conflitto
 * con altri slot o con visite già confermate.
 *
 * Fornisce un metodo {@link #getErrors()} usato dal GlobalExceptionHandler
 * per ottenere la mappa campo->messaggio da ritornare al client.
 */
public class AgentAvailabilityConflictException extends RuntimeException {
    private final Map<String, String> errors;

    public AgentAvailabilityConflictException(Map<String, String> errors) {
        super("Agent availability conflict");
        this.errors = errors == null ? Collections.emptyMap() : Map.copyOf(errors);
    }

    /**
     * Restituisce la mappa campo->messaggio da esporre nella risposta HTTP.
     */
    public Map<String, String> getErrors() {
        return errors;
    }
}