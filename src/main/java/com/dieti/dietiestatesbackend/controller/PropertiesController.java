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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.dieti.dietiestatesbackend.dto.response.PropertyResponse;
import com.dieti.dietiestatesbackend.entities.Address;
import com.dieti.dietiestatesbackend.security.AccessTokenProvider;
import com.dieti.dietiestatesbackend.service.AddressService;
import com.dieti.dietiestatesbackend.service.PropertyService;

@RestController
public class PropertiesController {
    private static final Logger logger = Logger.getLogger(PropertiesController.class.getName());
    private final PropertyService propertyService;
    private final AddressService addressService;

    @Autowired
    public PropertiesController(PropertyService propertyService, AddressService addressService) {
        this.propertyService = propertyService;
        this.addressService = addressService;
    }

    @GetMapping("/properties/search/{keyword}")
    public ResponseEntity<Object> getProperties(
            @PathVariable("keyword") String keyword,
            @RequestHeader(value = "Bearer", required = true) String accessToken) {
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
            return ResponseEntity.internalServerError().body(new ArrayList<PropertyResponse>());
        }
    }

    @GetMapping("/properties/details/{id}")
    public ResponseEntity<Object> getPropertyDetail(@PathVariable("id") long propertyID) throws SQLException {
        PropertyResponse p = propertyService.getProperty(propertyID);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }
        Address a = addressService.getAddress(p.getId_address());
        p.setAddress(a.toString());
        p.setLongitude(a.getLongitude());
        p.setLatitude(a.getLatitude());
        logger.info(p.getAddress());
        return ResponseEntity.ok(p);
    }

    @GetMapping("/thumbnails/{id}")
    public ResponseEntity<Resource> getThumbnails(@PathVariable("id") long propertyID) throws ResponseStatusException {
        Path path = Paths.get("/data/resources/listings/" + propertyID + "/01.jpg");
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
            logger.info(e.getMessage());
            return ResponseEntity.internalServerError().body(new ArrayList<PropertyResponse>());
        }
    }
}