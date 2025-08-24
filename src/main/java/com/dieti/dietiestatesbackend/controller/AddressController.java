package com.dieti.dietiestatesbackend.controller;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
 
import com.dieti.dietiestatesbackend.service.AddressService;
 
@RestController
public class AddressController {
    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);
    
    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/address/{id}")
    public ResponseEntity<Object> getAddress(@PathVariable("id") Long id) {
        return addressService.findById(id)
                .map(addr -> ResponseEntity.ok().body((Object) addr))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}