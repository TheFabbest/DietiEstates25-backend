package com.dieti.dietiestatesbackend.service;

import com.dieti.dietiestatesbackend.security.RefreshTokenProvider;
import com.dieti.dietiestatesbackend.security.RefreshTokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class AuthService {

    private static final Logger logger = Logger.getLogger(AuthService.class.getName());

    public LogoutResult logout(String refreshToken) {
        try {
            // Validazione del refreshToken
            if (refreshToken == null || refreshToken.isEmpty()) {
                return new LogoutResult(false, "Il refreshToken è obbligatorio", HttpStatus.BAD_REQUEST);
            }

            if (!RefreshTokenProvider.validateToken(refreshToken)) {
                return new LogoutResult(false, "Il refreshToken non è valido o è scaduto", HttpStatus.BAD_REQUEST);
            }

            // Estrazione dello username dal refreshToken
            String username = RefreshTokenProvider.getUsernameFromToken(refreshToken);

            // Verifica che il token appartenga effettivamente all'utente
            if (!RefreshTokenProvider.isTokenOf(username, refreshToken)) {
                return new LogoutResult(false, "Il refreshToken non corrisponde all'utente", HttpStatus.BAD_REQUEST);
            }

            // Tentativo di eliminazione del token
            RefreshTokenRepository.deleteUserToken(username, refreshToken);

            // Verifica se il token è stato effettivamente eliminato
            if (!RefreshTokenProvider.isTokenOf(username, refreshToken)) {
                return new LogoutResult(true, "Logout effettuato con successo", HttpStatus.OK);
            } else {
                // Caso in cui il token non è stato trovato o eliminato
                return new LogoutResult(false, "Token non trovato o già eliminato", HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore durante il logout: {0}", e.getMessage());
            return new LogoutResult(false, "Errore interno durante il logout", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static class LogoutResult {
        private final boolean success;
        private final String message;
        private final HttpStatus status;

        public LogoutResult(boolean success, String message, HttpStatus status) {
            this.success = success;
            this.message = message;
            this.status = status;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public HttpStatus getStatus() {
            return status;
        }
    }
}