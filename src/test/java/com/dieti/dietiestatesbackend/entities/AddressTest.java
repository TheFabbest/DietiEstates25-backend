package com.dieti.dietiestatesbackend.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AddressTest {
    
    @Test
    void testToStringOnNullFields () {
        Address address = new Address();
        String result = address.toString();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
