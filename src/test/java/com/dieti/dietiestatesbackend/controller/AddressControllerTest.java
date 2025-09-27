package com.dieti.dietiestatesbackend.controller;

import com.dieti.dietiestatesbackend.entities.Address;
import com.dieti.dietiestatesbackend.service.AddressService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertTrue(response.getBody() != null);
        assertEquals(address.getId(), response.getBody().getId());
        assertEquals(address.getStreet(), response.getBody().getStreet());
        assertEquals(address.getCity(), response.getBody().getCity());
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
        assertTrue(response.getBody() == null);
    }
}