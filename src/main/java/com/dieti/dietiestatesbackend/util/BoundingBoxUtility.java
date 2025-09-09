package com.dieti.dietiestatesbackend.util;

import java.math.BigDecimal;

/**
 * Utility class for precise bounding box calculations based on WGS84 ellipsoid.
 * Provides methods to calculate geographic bounding boxes from center coordinates and radius.
 * Handles edge cases like poles and antimeridian crossing.
 */
public final class BoundingBoxUtility {

    // WGS84 constants for precise geodetic calculations
    private static final double METERS_PER_DEGREE_LATITUDE = 111320.0; // ~111.32 km in meters
    private static final double MIN_COS_LATITUDE = 1e-10; // Minimum cosine value to avoid division by zero

    private BoundingBoxUtility() {
        // Utility class - prevent instantiation
    }

    /**
     * Calculate precise bounding box coordinates from center point and radius.
     * Based on WGS84 ellipsoid model with proper handling of latitude-dependent longitude spacing.
     *
     * @param centerLatitude Center latitude in degrees
     * @param centerLongitude Center longitude in degrees
     * @param radiusMeters Search radius in meters
     * @return Array of [minLat, maxLat, minLon, maxLon] in degrees
     * @throws IllegalArgumentException if coordinates are invalid
     */
    public static BigDecimal[] calculateBoundingBox(BigDecimal centerLatitude, BigDecimal centerLongitude, double radiusMeters) {
        validateInput(centerLatitude, centerLongitude, radiusMeters);

        double centerLatValue = centerLatitude.doubleValue();
        double centerLonValue = centerLongitude.doubleValue();

        // Calculate latitude delta (constant for all latitudes)
        double latDelta = calculateLatitudeDelta(radiusMeters);
        
        // Calculate longitude delta (varies with latitude)
        double lonDelta = calculateLongitudeDelta(centerLatValue, radiusMeters);

        // Calculate bounding box limits with validation
        double minLat = Math.max(-90.0, centerLatValue - latDelta);
        double maxLat = Math.min(90.0, centerLatValue + latDelta);
        double minLon = centerLonValue - lonDelta;
        double maxLon = centerLonValue + lonDelta;

        // Handle antimeridian crossing (longitude wrapping around ±180°)
        double[] adjustedLongitudes = adjustForAntimeridian(minLon, maxLon);

        return new BigDecimal[] {
            BigDecimal.valueOf(minLat),
            BigDecimal.valueOf(maxLat),
            BigDecimal.valueOf(adjustedLongitudes[0]),
            BigDecimal.valueOf(adjustedLongitudes[1])
        };
    }

    /**
     * Calculate latitude delta for bounding box.
     * Latitude spacing is approximately constant (~111.32 km per degree).
     *
     * @param radiusMeters Search radius in meters
     * @return Latitude delta in degrees
     */
    private static double calculateLatitudeDelta(double radiusMeters) {
        return radiusMeters / METERS_PER_DEGREE_LATITUDE;
    }

    /**
     * Calculate longitude delta for bounding box.
     * Longitude spacing varies with cosine of latitude.
     *
     * @param centerLatitude Center latitude in degrees
     * @param radiusMeters Search radius in meters
     * @return Longitude delta in degrees
     */
    private static double calculateLongitudeDelta(double centerLatitude, double radiusMeters) {
        double centerLatRad = Math.toRadians(centerLatitude);
        double cosLat = Math.cos(centerLatRad);
        
        // Avoid division by zero for latitudes very close to poles (±90°)
        if (Math.abs(cosLat) < MIN_COS_LATITUDE) {
            cosLat = MIN_COS_LATITUDE * Math.signum(cosLat);
        }
        
        return radiusMeters / (METERS_PER_DEGREE_LATITUDE * Math.abs(cosLat));
    }

    /**
     * Adjust longitude limits for antimeridian crossing (±180°).
     *
     * @param minLon Minimum longitude before adjustment
     * @param maxLon Maximum longitude before adjustment
     * @return Array of [adjustedMinLon, adjustedMaxLon]
     */
    private static double[] adjustForAntimeridian(double minLon, double maxLon) {
        // Handle wrapping around -180°/180° boundary
        if (minLon < -180.0) {
            minLon += 360.0;
        }
        if (maxLon > 180.0) {
            maxLon -= 360.0;
        }
        
        return new double[]{minLon, maxLon};
    }

    /**
     * Validate input parameters for bounding box calculation.
     *
     * @param centerLatitude Center latitude
     * @param centerLongitude Center longitude
     * @param radiusMeters Search radius
     * @throws IllegalArgumentException if any parameter is invalid
     */
    private static void validateInput(BigDecimal centerLatitude, BigDecimal centerLongitude, double radiusMeters) {
        if (centerLatitude == null || centerLongitude == null) {
            throw new IllegalArgumentException("Center coordinates cannot be null");
        }
        
        double lat = centerLatitude.doubleValue();
        double lon = centerLongitude.doubleValue();
        
        if (lat < -90 || lat > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
        }
        if (lon < -180 || lon > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
        }
        if (radiusMeters <= 0) {
            throw new IllegalArgumentException("Radius must be positive");
        }
        if (Double.isNaN(radiusMeters)) {
            throw new IllegalArgumentException("Radius cannot be NaN");
        }
    }

    /**
     * Check if a point is within the calculated bounding box.
     * Useful for additional validation or client-side checks.
     *
     * @param pointLat Point latitude to check
     * @param pointLon Point longitude to check
     * @param boundingBox Bounding box coordinates [minLat, maxLat, minLon, maxLon]
     * @return true if the point is within the bounding box, false otherwise
     */
    public static boolean isPointInBoundingBox(double pointLat, double pointLon, BigDecimal[] boundingBox) {
        if (boundingBox == null || boundingBox.length != 4) {
            return false;
        }
        
        double minLat = boundingBox[0].doubleValue();
        double maxLat = boundingBox[1].doubleValue();
        double minLon = boundingBox[2].doubleValue();
        double maxLon = boundingBox[3].doubleValue();
        
        // Handle antimeridian case where minLon > maxLon
        if (minLon > maxLon) {
            // Bounding box crosses antimeridian, check both segments
            return (pointLat >= minLat && pointLat <= maxLat) &&
                   ((pointLon >= minLon && pointLon <= 180.0) ||
                    (pointLon >= -180.0 && pointLon <= maxLon));
        } else {
            // Normal bounding box
            return pointLat >= minLat && pointLat <= maxLat &&
                   pointLon >= minLon && pointLon <= maxLon;
        }
    }
}