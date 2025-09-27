package com.dieti.dietiestatesbackend.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dieti.dietiestatesbackend.dto.response.AddressResponseDTO;
import com.dieti.dietiestatesbackend.dto.response.AgentResponseDTO;
import com.dieti.dietiestatesbackend.dto.response.CommercialPropertyResponse;
import com.dieti.dietiestatesbackend.entities.Address;
import com.dieti.dietiestatesbackend.entities.CommercialProperty;
import com.dieti.dietiestatesbackend.entities.User;

@ExtendWith(MockitoExtension.class)
public class CommercialPropertyMapperTest {

    @Mock
    private AddressMapper addressMapper;
    @Mock
    private AgentMapper agentMapper;
    @Mock
    private MapStructPropertyMapper mapStructPropertyMapper;

    @InjectMocks
    private CommercialPropertyMapperImpl mapper;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void testToResponse() {
        when(addressMapper.toDto(any(Address.class))).thenReturn(new AddressResponseDTO());
        when(agentMapper.toAgent(any(User.class))).thenReturn(new AgentResponseDTO());
        when(mapStructPropertyMapper.contractToString(any())).thenReturn("CONTRACT_STRING");
        when(mapStructPropertyMapper.propertyCategoryToString(any())).thenReturn("PROPERTY_CATEGORY_STRING");


        CommercialProperty property = new CommercialProperty();
        property.setFloor(2);
        property.setHasWheelchairAccess(true);
        property.setNumeroVetrine(3);
        property.setNumberOfFloors(5);
        property.setNumberOfRooms(2);
        property.setNumberOfBathrooms(1); // Added for completeness
        property.setAddress(new Address());
        property.setAgent(new User());
        property.setDescription("A commercial property");

        CommercialPropertyResponse response = mapper.toResponse(property);

        assertEquals(property.getFloor(), response.getFloor());
        assertEquals(property.getHasWheelchairAccess(), response.isHasDisabledAccess());
        assertEquals(property.getNumeroVetrine(), response.getShopWindowCount());
        assertEquals(property.getNumberOfFloors(), response.getTotalFloors());
        assertEquals(property.getNumberOfRooms(), response.getNumberOfRooms());
        assertEquals(property.getNumberOfBathrooms(), response.getNumberOfBathrooms());
        assertEquals(property.getDescription(), response.getDescription());
        assertNotNull(response.getAddress());
        assertNotNull(response.getAgent());
    }
}
