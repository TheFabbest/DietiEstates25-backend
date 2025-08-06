package com.dieti.dietiestatesbackend.controller;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.dieti.dietiestatesbackend.dto.Listing;
import com.dieti.dietiestatesbackend.dto.response.PropertyResponse;
import com.dieti.dietiestatesbackend.entities.Address;
import com.dieti.dietiestatesbackend.security.AccessTokenProvider;
import com.dieti.dietiestatesbackend.service.AddressService;
import com.dieti.dietiestatesbackend.service.PropertyService;

@RestController
@RequestMapping("/api")
public class ListingController {
    private static final Logger logger = Logger.getLogger(ListingController.class.getName());
    private final PropertyService propertyService;
    private final AddressService addressService;

    @Autowired
    public ListingController(PropertyService propertyService, AddressService addressService) {
        this.propertyService = propertyService;
        this.addressService = addressService;
    }

    @GetMapping("/properties/search/{keyword}")
    public ResponseEntity<Object> getListings(
            @PathVariable("keyword") String keyword,
            @RequestHeader(value = "Bearer", required = false) String accessToken) {
        if (accessToken == null || !AccessTokenProvider.validateToken(accessToken)) {
            return new ResponseEntity<>("Token non valido o scaduto", HttpStatusCode.valueOf(498));
        }
        try {
            List<PropertyResponse> list = propertyService.searchProperties(keyword);
            for (PropertyResponse p : list) {
                Address a = addressService.getAddress(p.getId_address());
                p.setAddress(a.toString());
                p.setLongitude(a.getLongitude());
                p.setLatitude(a.getLatitude());
            }
            return ResponseEntity.ok(list);
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante la ricerca degli immobili: {0}", e.getMessage());
            return ResponseEntity.internalServerError().body(new ArrayList<Listing>());
        }
    }

    @GetMapping("/thumbnails/{id}")
    public ResponseEntity<Resource> getThumbnails(@PathVariable("id") long listingID) throws ResponseStatusException {
        Path path = Paths.get("/data/resources/listings/" + listingID + "/01.jpg");
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, "URL malformato! {0}", e.getMessage());
        }
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(resource);
    }

    @GetMapping("/properties/featured")
    public ResponseEntity<Object> getFeatured() throws ResponseStatusException {
        try {
            return ResponseEntity.ok(propertyService.getFeatured());
        }
        catch (SQLException e) {
            return ResponseEntity.internalServerError().body(new ArrayList<Listing>());
        }
    }
}