package com.dieti.dietiestatesbackend.service;
 
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
 
import java.math.BigDecimal;
import java.util.Optional;
 
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

import com.dieti.dietiestatesbackend.entities.Address;
import com.dieti.dietiestatesbackend.entities.Coordinates;
import com.dieti.dietiestatesbackend.repositories.AddressRepository;
import com.dieti.dietiestatesbackend.service.geocoding.GeocodingService;
import com.dieti.dietiestatesbackend.dto.request.AddressRequest;
import com.dieti.dietiestatesbackend.exception.GeocodingException;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class AddressServiceImplTest {
 
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private GeocodingService geocodingService;

    private AddressServiceImpl addressService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        addressService = new AddressServiceImpl(addressRepository, geocodingService);
    }
 
    @Test
    void testCreateFromRequest() {
        AddressRequest request = new AddressRequest("Italy", "Rome", "Rome", "Via Roma", "1", "Building A");
        Address addressToGeocode = new Address();
        addressToGeocode.setCountry(request.getCountry());
        addressToGeocode.setProvince(request.getProvince());
        addressToGeocode.setCity(request.getCity());
        addressToGeocode.setStreet(request.getStreet());
        addressToGeocode.setStreetNumber(request.getStreetNumber());
        addressToGeocode.setBuilding(request.getBuilding());

        Coordinates coordinates = new Coordinates();
        coordinates.setLatitude(BigDecimal.valueOf(10.0));
        coordinates.setLongitude(BigDecimal.valueOf(20.0));

        when(geocodingService.geocode(any(Address.class))).thenReturn(Optional.of(coordinates));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address argAddress = invocation.getArgument(0);
            argAddress.setId(1L); // Simulate ID being set after saving
            return argAddress;
        });

        Address createdAddress = addressService.createFromRequest(request);

        assertNotNull(createdAddress);
        assertNotNull(createdAddress.getId());
        assertEquals(request.getCountry(), createdAddress.getCountry());
        assertEquals(request.getCity(), createdAddress.getCity());
        assertNotNull(createdAddress.getCoordinates());
        assertEquals(coordinates.getLatitude(), createdAddress.getCoordinates().getLatitude());
        assertEquals(coordinates.getLongitude(), createdAddress.getCoordinates().getLongitude());

        verify(geocodingService).geocode(any(Address.class));
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void testCreateFromRequest_GeocodingFails() {
        AddressRequest request = new AddressRequest("Italy", "Rome", "Rome", "Via Roma", "1", "Building A");
        
        when(geocodingService.geocode(any(Address.class))).thenReturn(Optional.empty());

        GeocodingException thrown = assertThrows(GeocodingException.class, () -> {
            addressService.createFromRequest(request);
        });

        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());

        verify(geocodingService).geocode(any(Address.class));
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void testCreateFromRequest_NullRequest() {
        assertThrows(IllegalArgumentException.class, () -> {
            addressService.createFromRequest(null);
        });
        
        verify(geocodingService, never()).geocode(any(Address.class));
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void testCreateFromRequest_CreatedAtIsSet() {
        AddressRequest request = new AddressRequest("Italy", "Rome", "Rome", "Via Roma", "1", "Building A");
        Coordinates coordinates = new Coordinates();
        coordinates.setLatitude(BigDecimal.valueOf(10.0));
        coordinates.setLongitude(BigDecimal.valueOf(20.0));

        when(geocodingService.geocode(any(Address.class))).thenReturn(Optional.of(coordinates));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address argAddress = invocation.getArgument(0);
            argAddress.setId(1L); // Simulate ID being set after saving
            return argAddress;
        });

        Address createdAddress = addressService.createFromRequest(request);

        assertNotNull(createdAddress.getCreatedAt());
        verify(geocodingService).geocode(any(Address.class));
        verify(addressRepository).save(any(Address.class));
    }
 
    @Test
    void testFindById() {
        Long addressId = 1L;
        Address address = new Address();
        address.setId(addressId);
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));

        Optional<Address> foundAddress = addressService.findById(addressId);

        assertNotNull(foundAddress);
        assertEquals(foundAddress.get(), address);
        verify(addressRepository).findById(addressId);
    }

    @Test
    void testFindById_NullId() {
        Optional<Address> foundAddress = addressService.findById(null);

        assertNotNull(foundAddress);
        assertEquals(Optional.empty(), foundAddress);
        verify(addressRepository, never()).findById(anyLong());
    }
 
    @Test
    void testGeocodeAddress() {
        Address address = new Address();
        address.setCountry("Italy");
        address.setCity("Rome");
        address.setStreet("Via Roma");
        address.setStreetNumber("1");
        
        Coordinates coordinates = new Coordinates();
        coordinates.setLatitude(BigDecimal.valueOf(10.0));
        coordinates.setLongitude(BigDecimal.valueOf(20.0));

        when(geocodingService.geocode(any(Address.class))).thenReturn(Optional.of(coordinates));

        Address geocodedAddress = addressService.geocodeAddress(address);

        assertNotNull(geocodedAddress);
        assertNotNull(geocodedAddress.getCoordinates());
        assertEquals(coordinates.getLatitude(), geocodedAddress.getCoordinates().getLatitude());
        assertEquals(coordinates.getLongitude(), geocodedAddress.getCoordinates().getLongitude());
        verify(geocodingService).geocode(any(Address.class));
    }

    @Test
    void testGeocodeAddress_GeocodingFails() {
        Address address = new Address();
        address.setCountry("Italy");
        address.setCity("Rome");
        address.setStreet("Via Roma");
        address.setStreetNumber("1");
        
        when(geocodingService.geocode(any(Address.class))).thenReturn(Optional.empty());

        GeocodingException thrown = assertThrows(GeocodingException.class, () -> {
            addressService.geocodeAddress(address);
        });

        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());

        verify(geocodingService).geocode(any(Address.class));
    }

    @Test
    void testGeocodeAddress_NullAddress() {
        assertThrows(IllegalArgumentException.class, () -> {
            addressService.geocodeAddress(null);
        });
        
        verify(geocodingService, never()).geocode(any(Address.class));
    }
}
