package com.dieti.dietiestatesbackend.exception;
 
import java.util.Map;
 
/**
 * Eccezione specifica per violazioni delle regole di overbooking.
 * Trasporta una mappa campo->messaggio per il client.
 *
 * La mappa errors è ora obbligatoria (non-null e non-vuota) per garantire
 * risposte informative al client e log coerenti.
 */
public class OverbookingException extends RuntimeException {
 
    private final Map<String, String> errors;
 
    public OverbookingException(Map<String, String> errors) {
        super("Overbooking");
        if (errors == null || errors.isEmpty()) {
            throw new IllegalArgumentException("errors map must be non-null and contain at least one entry");
        }
        // Rendiamo la mappa immutabile per sicurezza e comportamento difensivo
        this.errors = Map.copyOf(errors);
    }
 
    public Map<String, String> getErrors() {
        return errors;
    }
}