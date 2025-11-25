package com.dieti.dietiestatesbackend.controller;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.dieti.dietiestatesbackend.entities.Address;
import com.dieti.dietiestatesbackend.service.AddressService;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest {

    @InjectMocks
    private AddressController addressController;

    @Mock
    private AddressService addressService;

    @Test
    void getAddress_shouldReturnAddress_whenAddressExists() {
        // Given
        Long addressId = 1L;
        Address address = new Address();
        address.setId(addressId);
        address.setStreet("Via Roma");
        address.setStreetNumber("1");
        address.setCity("Rome");
        address.setProvince("RM");
        address.setCountry("Italy");
        address.setCoordinates(new com.dieti.dietiestatesbackend.entities.Coordinates(java.math.BigDecimal.valueOf(41.902782), java.math.BigDecimal.valueOf(12.496366)));

        when(addressService.findById(anyLong())).thenReturn(Optional.of(address));

        // When
        ResponseEntity<Address> response = addressController.getAddress(addressId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var body = response.getBody();
        assertNotNull(body);
        assertEquals(address.getId(), body.getId());
        assertEquals(address.getStreet(), body.getStreet());
        assertEquals(address.getCity(), body.getCity());
    }

    @Test
    void getAddress_shouldReturnNotFound_whenAddressDoesNotExist() {
        // Given
        Long addressId = 1L;
        when(addressService.findById(anyLong())).thenReturn(Optional.empty());

        // When
        ResponseEntity<Address> response = addressController.getAddress(addressId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}