package com.dieti.dietiestatesbackend.util;

import java.math.BigDecimal;

import com.dieti.dietiestatesbackend.entities.Coordinates;

/**
 * Utility class for calculating distances between geographic coordinates
 * using the Haversine formula.
 * Utilizza la validazione centralizzata delle coordinate dalla classe Coordinates.
 */
public final class HaversineUtils {

    private static final double EARTH_RADIUS_METERS = 6371000.0; // Earth radius in meters

    private HaversineUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Calculates the distance between two geographic points using the Haversine formula.
     *
     * @param lat1 latitude of first point in degrees
     * @param lon1 longitude of first point in degrees
     * @param lat2 latitude of second point in degrees
     * @param lon2 longitude of second point in degrees
     * @return distance in meters between the two points
     * @throws IllegalArgumentException if any coordinate is null or out of valid range
     */
    public static double calculateDistance(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2) {
        Coordinates.validateCoordinates(lat1, lon1);
        Coordinates.validateCoordinates(lat2, lon2);

        // Convert degrees to radians
        double lat1Rad = Math.toRadians(lat1.doubleValue());
        double lon1Rad = Math.toRadians(lon1.doubleValue());
        double lat2Rad = Math.toRadians(lat2.doubleValue());
        double lon2Rad = Math.toRadians(lon2.doubleValue());

        // Haversine formula
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METERS * c;
    }
}