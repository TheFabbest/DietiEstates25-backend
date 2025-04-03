package com.dieti.dietiestatesbackend.controller;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import com.dieti.dietiestatesbackend.security.AccessTokenProvider;

@RestController
@RequestMapping("/api")
public class ListingController {
    private static final Logger logger = Logger.getLogger(ListingController.class.getName());

    @GetMapping("/listings/{keyword}")
    public ResponseEntity<Object> getListings(
            @PathVariable("keyword") String keyword,
            @RequestHeader(value = "Bearer", required = false) String accessToken) {
        if (accessToken == null || !AccessTokenProvider.validateToken(accessToken)) {
            return new ResponseEntity<>("Token non valido o scaduto", HttpStatusCode.valueOf(498));
        }
        return ResponseEntity.ok(Arrays.asList(
            new Listing(1, "Castello di Hogwarts",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor", 
                "Napoli (NA)", 3500000f),
            new Listing(1, "Casa dello Hobbit", 
                "Lorem ipsum", 
                "Pioppaino (NA)", 1350000f)
        ));
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
}