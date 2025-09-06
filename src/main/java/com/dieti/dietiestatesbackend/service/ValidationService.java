package com.dieti.dietiestatesbackend.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

/**
 * Servizio centralizzato per la validazione dei DTO.
 * Estrae la logica di validazione dal PropertyCreationService per migliorare
 * testabilità e separazione delle responsabilità.
 */
@Service
public class ValidationService {

    private static final Logger logger = LoggerFactory.getLogger(ValidationService.class);

    private final Validator validator;

    public ValidationService(Validator validator) {
        this.validator = validator;
    }

    /**
     * Valida l'oggetto passato e lancia IllegalArgumentException con messaggio
     * composto se ci sono violazioni.
     */
    public <T> void validate(T request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }

        // Bad smell: cast necessario per evitare warning di unchecked conversion
        @SuppressWarnings("unchecked")
        Set<ConstraintViolation<T>> violations = (Set<ConstraintViolation<T>>) (Set<?>) validator.validate(request);

        if (!violations.isEmpty()) {
            String msg = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining("; "));
            logger.debug("Validation failed: {}", msg);
            throw new IllegalArgumentException("Validation failed: " + msg);
        }
    }
}