package com.dieti.dietiestatesbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.NoHandlerFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Gestore globale delle eccezioni per l'applicazione DietiEstates25.
 * Fornisce una gestione centralizzata degli errori per evitare la ripetizione
 * della logica di gestione degli errori in ogni controller.
 */
@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gestisce l'eccezione EntityNotFoundException.
     * Restituisce una risposta HTTP 404 Not Found con un messaggio generico.
     *
     * @param ex L'eccezione EntityNotFoundException catturata
     * @return ResponseEntity con status 404 e un messaggio di errore generico
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Risorsa non trovata",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Gestisce l'eccezione HttpMessageNotReadableException, che si verifica quando il corpo della richiesta non può essere letto (es. JSON malformato).
     * Restituisce una risposta HTTP 400 Bad Request.
     *
     * @param ex L'eccezione HttpMessageNotReadableException catturata
     * @return ResponseEntity con status 400 e un messaggio di errore specifico
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        LoggerFactory.getLogger(GlobalExceptionHandler.class).error("Errore di formato della richiesta (JSON malformato?): {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Richiesta malformata",
                "Il corpo della richiesta non è leggibile o è malformato. Verificare la sintassi JSON.",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Gestisce le eccezioni di autorizzazione (Access Denied).
     * Restituisce HTTP 403 Forbidden con un messaggio chiaro.
     *
     * @param ex L'eccezione AuthorizationDeniedException catturata
     * @return ResponseEntity con status 403 e messaggio di accesso negato
     */
    @ExceptionHandler(org.springframework.security.authorization.AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDenied(org.springframework.security.authorization.AuthorizationDeniedException ex) {
        // Log a concise warning for access denied events
        LoggerFactory.getLogger(GlobalExceptionHandler.class).warn("Accesso negato: {}", ex.getMessage(), ex);
 
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Accesso negato",
                "Non hai i permessi per accedere a questa risorsa.",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
 
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Credenziali non valide",
                "Le credenziali fornite non sono corrette.",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Richiesta non valida",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        // concat all error messages into a single string
        String errors = ex.getBindingResult().getAllErrors().stream()
            .map(ObjectError::getDefaultMessage)
            .collect(Collectors.joining("; "));
        ArrayList<String> args = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField())
            .collect(Collectors.toCollection(ArrayList::new));

        LoggerFactory.getLogger(GlobalExceptionHandler.class).info("Errori di validazione: {} campi non validi", ex.getErrorCount());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "message", errors,
                    "fields", args.toString()
                ));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Stato non valido",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({IOException.class, GeneralSecurityException.class})
    public ResponseEntity<ErrorResponse> handleExternalAuthErrors(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                498,
                "Token Google non valido",
                "Il token fornito non è valido o è scaduto: " + ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(org.springframework.http.HttpStatusCode.valueOf(498)).body(errorResponse);
    }
 
    /**
     * Gestisce tutte le altre eccezioni non gestite specificamente.
     * Restituisce una risposta HTTP 500 Internal Server Error con un messaggio generico.
     *
     * @param ex L'eccezione generica catturata
     * @return ResponseEntity con status 500 e un messaggio di errore generico
     */
    @ExceptionHandler(GeocodingException.class)
    public ResponseEntity<ErrorResponse> handleGeocodingException(GeocodingException ex) {
        LoggerFactory.getLogger(GlobalExceptionHandler.class).error("Errore geocoding: {}", ex.getMessage(), ex);
        HttpStatus status = ex.getHttpStatus() != null ? ex.getHttpStatus() : HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                "Errore geocoding",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Gestisce l'eccezione PlacesServiceException per errori relativi al servizio Places.
     * Restituisce una risposta HTTP 500 Internal Server Error con messaggio descrittivo.
     *
     * @param ex L'eccezione PlacesServiceException catturata
     * @return ResponseEntity con status 500 e messaggio di errore
     */
    @ExceptionHandler(PlacesServiceException.class)
    public ResponseEntity<ErrorResponse> handlePlacesServiceException(PlacesServiceException ex) {
        LoggerFactory.getLogger(GlobalExceptionHandler.class).error("Errore nel servizio Places: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Errore nel servizio di ricerca luoghi",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Gestisce l'eccezione SpatialSearchException per errori relativi alla ricerca spaziale.
     * Restituisce una risposta HTTP appropriata basata sullo status definito nell'eccezione.
     * Segue il principio Open/Closed essendo facilmente estendibile per nuove eccezioni.
     *
     * @param ex L'eccezione SpatialSearchException catturata
     * @return ResponseEntity con status appropriato e messaggio di errore
     */
    @ExceptionHandler(SpatialSearchException.class)
    public ResponseEntity<ErrorResponse> handleSpatialSearchException(SpatialSearchException ex) {
        LoggerFactory.getLogger(GlobalExceptionHandler.class).warn("Errore ricerca spaziale: {}", ex.getMessage(), ex);
        HttpStatus status = ex.getHttpStatus() != null ? ex.getHttpStatus() : HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                "Errore ricerca geografica",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Gestisce l'eccezione InvalidImageException per errori di validazione delle immagini.
     * Restituisce una risposta HTTP 400 Bad Request con messaggio descrittivo.
     *
     * @param ex L'eccezione InvalidImageException catturata
     * @return ResponseEntity con status 400 e messaggio di errore
     */
    @ExceptionHandler(InvalidImageException.class)
    public ResponseEntity<ErrorResponse> handleInvalidImageException(InvalidImageException ex) {
        LoggerFactory.getLogger(GlobalExceptionHandler.class).warn("Errore validazione immagine: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Immagine non valida",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Gestisce l'eccezione StorageException per errori durante le operazioni di storage.
     * Restituisce una risposta HTTP 500 Internal Server Error con messaggio descrittivo.
     *
     * @param ex L'eccezione StorageException catturata
     * @return ResponseEntity con status 500 e messaggio di errore
     */
    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorResponse> handleStorageException(StorageException ex) {
        LoggerFactory.getLogger(GlobalExceptionHandler.class).error("Errore storage: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Errore durante l'operazione di storage",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Gestisce l'eccezione NoHandlerFoundException, che si verifica quando un endpoint non viene trovato.
     * Restituisce una risposta HTTP 404 Not Found con un messaggio informativo.
     *
     * @param ex L'eccezione NoHandlerFoundException catturata
     * @return ResponseEntity con status 404 e un messaggio di endpoint non trovato
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        LoggerFactory.getLogger(GlobalExceptionHandler.class).warn("Endpoint non trovato: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Endpoint non trovato",
                "L'endpoint richiesto non esiste: " + ex.getRequestURL(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        // Log the full exception for debugging purposes
        LoggerFactory.getLogger(GlobalExceptionHandler.class).error("Errore interno del server: {}", ex.getMessage(), ex);
 
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Errore interno del server",
                "Si è verificato un errore imprevisto. Riprovare più tardi.",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Gestisce gli errori di validazione semantica raccolti in InvalidPayloadException.
     * Restituisce una mappa campo->messaggio con status 400 Bad Request.
     */
    @ExceptionHandler(InvalidPayloadException.class)
    public ResponseEntity<Map<String, String>> handleInvalidPayload(InvalidPayloadException ex) {
        Map<String, String> errors = ex.getErrors();
        LoggerFactory.getLogger(GlobalExceptionHandler.class).info("Invalid payload: {} errors", errors.size());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Gestisce l'eccezione OverbookingException per violazioni delle regole di overbooking.
     * Restituisce HTTP 409 Conflict con la mappa campo->messaggio contenuta nell'eccezione.
     *
     * @param ex L'eccezione OverbookingException catturata
     * @return ResponseEntity con status 409 e body contenente dettagli dell'overbooking
     */
    @ExceptionHandler(OverbookingException.class)
    public ResponseEntity<Map<String, String>> handleOverbookingException(OverbookingException ex) {
        Map<String, String> errors = ex.getErrors();
        LoggerFactory.getLogger(GlobalExceptionHandler.class).warn("Overbooking rilevato: {} dettagli", errors != null ? errors.size() : 0, ex);
 
        if (errors == null || errors.isEmpty()) {
            // Risposta strutturata minima se l'eccezione non contiene dettagli
            return new ResponseEntity<>(Map.of(
                    "error", "Overbooking",
                    "message", "La conferma della visita è in conflitto con le regole di disponibilità/overbooking"
            ), HttpStatus.CONFLICT);
        }
 
        return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
    }
    
    /**
     * Gestisce conflitti relativi alle disponibilità agente (AgentAvailabilityConflictException).
     * Restituisce HTTP 409 Conflict con la mappa campo->messaggio fornita dall'eccezione.
     */
    @ExceptionHandler(AgentAvailabilityConflictException.class)
    public ResponseEntity<Map<String, String>> handleAgentAvailabilityConflict(com.dieti.dietiestatesbackend.exception.AgentAvailabilityConflictException ex) {
        Map<String, String> errors = ex.getErrors();
        LoggerFactory.getLogger(GlobalExceptionHandler.class).warn("Agent availability conflict: {} details", errors != null ? errors.size() : 0, ex);
        if (errors == null || errors.isEmpty()) {
            return new ResponseEntity<>(Map.of("error", "AgentAvailabilityConflict", "message", "Conflitto nella gestione della disponibilità agente"), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
    }
 
    /**
     * Gestisce il caso in cui una disponibilità agente non venga trovata.
     * Restituisce HTTP 404 Not Found con un messaggio leggibile.
     */
    @ExceptionHandler(AgentAvailabilityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAgentAvailabilityNotFound(com.dieti.dietiestatesbackend.exception.AgentAvailabilityNotFoundException ex) {
        LoggerFactory.getLogger(GlobalExceptionHandler.class).info("Agent availability not found: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Disponibilità non trovata",
                ex.getMessage(),
                java.time.LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * DTO per la rappresentazione standardizzata degli errori.
     */
    public static class ErrorResponse {
        private int status;
        private String error;
        private String message;
        private LocalDateTime timestamp;

        public ErrorResponse(int status, String error, String message, LocalDateTime timestamp) {
            this.status = status;
            this.error = error;
            this.message = message;
            this.timestamp = timestamp;
        }

        // Getters
        public int getStatus() {
            return status;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}