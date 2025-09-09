package com.dieti.dietiestatesbackend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entità per rappresentare coordinate geografiche con validazione centralizzata.
 * Fornisce metodi di utilità per la validazione delle coordinate che possono essere
 * riutilizzati in tutta l'applicazione per evitare duplicazione di codice.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates {

    @Digits(integer = 10, fraction = 8)
    @DecimalMin(value = "-90.0", inclusive = true, message = "La latitudine deve essere maggiore o uguale a -90.0")
    @DecimalMax(value = "90.0", inclusive = true, message = "La latitudine deve essere minore o uguale a 90.0")
    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Digits(integer = 11, fraction = 8)
    @DecimalMin(value = "-180.0", inclusive = true, message = "La longitudine deve essere maggiore o uguale a -180.0")
    @DecimalMax(value = "180.0", inclusive = true, message = "La longitudine deve essere minore o uguale a 180.0")
    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    /**
     * Valida se le coordinate sono valide (non nulle e nei range corretti).
     * Questo metodo può essere utilizzato per la validazione centralizzata delle coordinate
     * in tutta l'applicazione, evitando duplicazione di codice.
     *
     * @param latitude latitudine da validare
     * @param longitude longitudine da validare
     * @throws IllegalArgumentException se le coordinate non sono valide
     */
    public static void validateCoordinates(BigDecimal latitude, BigDecimal longitude) {
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("Le coordinate non possono essere nulle");
        }
        
        validateLatitude(latitude);
        validateLongitude(longitude);
    }

    /**
     * Valida una latitudine.
     *
     * @param latitude latitudine da validare
     * @throws IllegalArgumentException se la latitudine non è valida
     */
    public static void validateLatitude(BigDecimal latitude) {
        if (latitude == null) {
            throw new IllegalArgumentException("La latitudine non può essere nulla");
        }
        double latValue = latitude.doubleValue();
        if (latValue < -90.0 || latValue > 90.0) {
            throw new IllegalArgumentException("La latitudine deve essere compresa tra -90.0 e 90.0 gradi");
        }
    }

    /**
     * Valida una longitudine.
     *
     * @param longitude longitudine da validare
     * @throws IllegalArgumentException se la longitudine non è valida
     */
    public static void validateLongitude(BigDecimal longitude) {
        if (longitude == null) {
            throw new IllegalArgumentException("La longitudine non può essere nulla");
        }
        double lonValue = longitude.doubleValue();
        if (lonValue < -180.0 || lonValue > 180.0) {
            throw new IllegalArgumentException("La longitudine deve essere compresa tra -180.0 e 180.0 gradi");
        }
    }

    /**
     * Verifica se le coordinate sono valide senza lanciare eccezioni.
     *
     * @param latitude latitudine da verificare
     * @param longitude longitudine da verificare
     * @return true se le coordinate sono valide, false altrimenti
     */
    public static boolean isValid(BigDecimal latitude, BigDecimal longitude) {
        try {
            validateCoordinates(latitude, longitude);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}