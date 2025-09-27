package com.dieti.dietiestatesbackend.entities;

import com.dieti.dietiestatesbackend.dto.response.AgentVisitDTO;
import com.dieti.dietiestatesbackend.repositories.VisitRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AddressTest {
    
    @Test
    void testToStringOnNullFields () {
        Address address = new Address();
        String result = address.toString();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
