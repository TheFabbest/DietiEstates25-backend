package com.dieti.dietiestatesbackend.util;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class BoundingBoxUtilityTest {

    private final BoundingBoxUtility util = new BoundingBoxUtility();

    @Test
    void testCalculateBoundingBoxAtEquator() {
        BigDecimal lat = BigDecimal.valueOf(0.0);
        BigDecimal lon = BigDecimal.valueOf(0.0);
        double radius = 1000.0;

        BigDecimal[] box = util.calculateBoundingBox(lat, lon, radius);

        double expectedDelta = radius / 111320.0;
        assertEquals(-expectedDelta, box[0].doubleValue(), 1e-9, "minLat mismatch");
        assertEquals(expectedDelta, box[1].doubleValue(), 1e-9, "maxLat mismatch");
        assertEquals(-expectedDelta, box[2].doubleValue(), 1e-9, "minLon mismatch");
        assertEquals(expectedDelta, box[3].doubleValue(), 1e-9, "maxLon mismatch");
    }

    @Test
    void testCalculateBoundingBoxAsDoubleMatchesBigDecimal() {
        BigDecimal lat = BigDecimal.valueOf(10.5);
        BigDecimal lon = BigDecimal.valueOf(-20.25);
        double radius = 5000.0;

        BigDecimal[] boxBd = util.calculateBoundingBox(lat, lon, radius);
        double[] boxD = util.calculateBoundingBoxAsDouble(lat, lon, radius);

        assertEquals(boxBd[0].doubleValue(), boxD[0], 1e-12);
        assertEquals(boxBd[1].doubleValue(), boxD[1], 1e-12);
        assertEquals(boxBd[2].doubleValue(), boxD[2], 1e-12);
        assertEquals(boxBd[3].doubleValue(), boxD[3], 1e-12);
    }

    @Test
    void testAntimeridianCrossingAndContainment() {
        BigDecimal lat = BigDecimal.valueOf(0.0);
        BigDecimal lon = BigDecimal.valueOf(179.9);
        double radius = 20000.0; // large enough to cross antimeridian at equator

        BigDecimal[] box = util.calculateBoundingBox(lat, lon, radius);

        double minLon = box[2].doubleValue();
        double maxLon = box[3].doubleValue();

        // Should wrap and produce minLon > maxLon indicating antimeridian crossing
        assertTrue(minLon > maxLon, "Expected antimeridian crossing (minLon > maxLon)");

        // Points near 179.95 and -179.95 should be inside
        assertTrue(util.isPointInBoundingBox(0.0, 179.95, box), "Point near 179.95 should be inside");
        assertTrue(util.isPointInBoundingBox(0.0, -179.95, box), "Point near -179.95 should be inside");

        // A distant point should be outside
        assertFalse(util.isPointInBoundingBox(0.0, 0.0, box), "Point at 0.0 should be outside");
    }

    @Test
    void testLatitudeClampingNearPole() {
        BigDecimal lat = BigDecimal.valueOf(89.99);
        BigDecimal lon = BigDecimal.valueOf(0.0);
        double radius = 20000.0;

        BigDecimal[] box = util.calculateBoundingBox(lat, lon, radius);

        // maxLat should be clamped to 90.0
        assertEquals(90.0, box[1].doubleValue(), 1e-9, "maxLat should be clamped to 90.0");
        // minLat should be less than maxLat and sensible
        assertTrue(box[0].doubleValue() < box[1].doubleValue(), "minLat should be less than maxLat");
    }

    @Test
    void testInvalidInputsThrow() {
        BigDecimal validLat = BigDecimal.valueOf(0.0);
        BigDecimal validLon = BigDecimal.valueOf(0.0);

        // null coordinates
        Executable executable = () -> util.calculateBoundingBox(null, validLon, 1000.0);
        assertThrows(IllegalArgumentException.class, executable);
        
        executable = () -> util.calculateBoundingBox(validLat, null, 1000.0);
        assertThrows(IllegalArgumentException.class, executable);

        // invalid latitude/longitude ranges
        executable = () -> util.calculateBoundingBox(BigDecimal.valueOf(91.0), validLon, 1000.0);
        assertThrows(IllegalArgumentException.class, executable);
        executable = () -> util.calculateBoundingBox(BigDecimal.valueOf(-91.0), validLon, 1000.0);
        assertThrows(IllegalArgumentException.class, executable);
        executable = () -> util.calculateBoundingBox(validLat, BigDecimal.valueOf(181.0), 1000.0);
        assertThrows(IllegalArgumentException.class, executable);
        executable = () -> util.calculateBoundingBox(validLat, BigDecimal.valueOf(-181.0), 1000.0);
        assertThrows(IllegalArgumentException.class, executable);
  
        // non-positive radius
        executable = () -> util.calculateBoundingBox(validLat, validLon, 0.0);
        assertThrows(IllegalArgumentException.class, executable);
        executable = () -> util.calculateBoundingBox(validLat, validLon, -1.0);
        assertThrows(IllegalArgumentException.class, executable);

        // NaN radius
        executable = () -> util.calculateBoundingBox(validLat, validLon, Double.NaN);
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void testIsPointInBoundingBoxNullOrMalformed() {
        // null bounding box
        assertFalse(util.isPointInBoundingBox(0.0, 0.0, null));
        // wrong length
        BigDecimal[] wrong = new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ONE };
        assertFalse(util.isPointInBoundingBox(0.0, 0.0, wrong));
    }
}