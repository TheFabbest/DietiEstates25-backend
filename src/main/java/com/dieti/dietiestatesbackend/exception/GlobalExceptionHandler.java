package com.dieti.dietiestatesbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;

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
                "La risorsa richiesta non è disponibile.",
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
 
    /**
     * Gestisce tutte le altre eccezioni non gestite specificamente.
     * Restituisce una risposta HTTP 500 Internal Server Error con un messaggio generico.
     *
     * @param ex L'eccezione generica catturata
     * @return ResponseEntity con status 500 e un messaggio di errore generico
     */
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