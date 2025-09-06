package com.dieti.dietiestatesbackend.service.geocoding;

import java.math.BigDecimal;

/**
 * Record che rappresenta le coordinate geografiche.
 */
public record Coordinates(BigDecimal latitude, BigDecimal longitude) {
}