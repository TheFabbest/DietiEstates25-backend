package com.dieti.dietiestatesbackend.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validatore per le password degli utenti.
 */
public class PasswordValidator {
    
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
    private static final Pattern PASSWORD_COMPILED_PATTERN = Pattern.compile(PASSWORD_REGEX);

    /**
     * Verifica se una password è forte secondo i criteri definiti.
     * 
     * @param password la password da validare
     * @return true se la password è forte, false altrimenti
     */
    public boolean isStrong(String password) {
        if (password == null) {
            return false;
        }
        final Matcher matcher = PASSWORD_COMPILED_PATTERN.matcher(password);
        return matcher.matches();
    }
}