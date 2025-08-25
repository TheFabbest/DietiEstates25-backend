package com.dieti.dietiestatesbackend.controller;

import java.net.MalformedURLException;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.dieti.dietiestatesbackend.dto.request.CreatePropertyRequest;
import com.dieti.dietiestatesbackend.dto.request.FilterRequest;
import com.dieti.dietiestatesbackend.dto.response.PropertyResponse;
import com.dieti.dietiestatesbackend.service.PropertyService;
import com.dieti.dietiestatesbackend.mappers.PropertyMapper;
import com.dieti.dietiestatesbackend.util.PropertyImageUtils;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

@RestController
public class PropertiesController {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesController.class);
    private final PropertyService propertyService;
    private final PropertyImageUtils propertyImageUtils;

    @Autowired
    public PropertiesController(PropertyService propertyService, PropertyImageUtils propertyImageUtils) {
        this.propertyService = propertyService;
        this.propertyImageUtils = propertyImageUtils;
    }

    @PostMapping("/properties/search/{keyword}")
    public ResponseEntity<Object> getProperties(
            @PathVariable("keyword") String keyword,
            @RequestBody FilterRequest filters) {
        return ResponseEntity.ok(propertyService.searchPropertiesWithFilters(keyword, filters).stream()
            .map(PropertyMapper::toResponse)
            .toList());
    }

    @PreAuthorize("@securityUtil.canAccessProperty(#authentication.principal, #propertyID)")
    @GetMapping("/properties/details/{id}")
    public ResponseEntity<Object> getPropertyDetail(@PathVariable("id") long propertyID) {
        PropertyResponse p = PropertyMapper.toResponse(propertyService.getProperty(propertyID));
        if (p == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(p);
    }

    @GetMapping("/thumbnails/{id}")
    public ResponseEntity<Resource> getThumbnails(@PathVariable("id") long propertyID) throws ResponseStatusException {
        Path path = propertyImageUtils.buildThumbnailPath(propertyID);
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            logger.error("URL malformato! {}", e.getMessage());
        }
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("image/webp"))
            .body(resource);
    }

    @GetMapping("/properties/featured")
    public ResponseEntity<Object> getFeatured() {
        return ResponseEntity.ok(propertyService.getFeatured().stream().map(PropertyMapper::toResponse).toList());
    }

    /**
     * Endpoint per la creazione di una nuova proprietà.
     * Accetta il DTO unificato CreatePropertyRequest nel body della richiesta,
     * valida l'input e delega la creazione al PropertyService.
     * Restituisce 201 Created con il PropertyResponse della risorsa creata.
     */
    @PostMapping("/properties")
    public ResponseEntity<PropertyResponse> createProperty(@Valid @RequestBody CreatePropertyRequest request) {
        logger.debug("Richiesta di creazione proprietà ricevuta: {}", request);
        logger.info("Payload completo ricevuto: {}", request);
        logger.debug("DEBUG LOG: Richiesta POST /properties ricevuta - inizio elaborazione");
        try {
            PropertyResponse created = propertyService.createProperty(request);
            logger.debug("Proprietà creata con successo: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            logger.error("Errore durante la creazione della proprietà per richiesta {}: tipo={}, messaggio={}",
                request, e.getClass().getSimpleName(), e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore durante la creazione della proprietà: " + e.getMessage(), e);
        }
    }

    // Gestione locale della validazione per restituire 400 con dettagli dei campi
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}