package com.dieti.dietiestatesbackend.exception;

/**
 * Eccezione lanciata quando uno slot di disponibilità non viene trovato.
 */
public class AgentAvailabilityNotFoundException extends RuntimeException {
    public AgentAvailabilityNotFoundException(Long id) {
        super("Agent availability not found with id: " + id);
    }
}