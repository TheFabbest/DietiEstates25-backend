package com.dieti.dietiestatesbackend.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dieti.dietiestatesbackend.dto.request.CreatePropertyRequest;
import com.dieti.dietiestatesbackend.dto.request.FilterRequest;
import com.dieti.dietiestatesbackend.dto.request.PropertyHistoryRequest;
import com.dieti.dietiestatesbackend.dto.response.PropertyResponse;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.PropertyCategory;
import com.dieti.dietiestatesbackend.mappers.ResponseMapperRegistry;
import com.dieti.dietiestatesbackend.security.AppPrincipal;
import com.dieti.dietiestatesbackend.service.PropertyService;
import com.dieti.dietiestatesbackend.service.lookup.CategoryLookupService;
import com.dieti.dietiestatesbackend.service.places.dto.PlaceDTO;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;


@RestController
public class PropertiesController {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesController.class);
    private static final String DEFAULT_RADIUS_SPEL = "#{T(java.lang.Integer).valueOf(${geoapify.places.radius})}";
    private static final String DEFAULT_CATEGORIES_SPEL = "#{'${geoapify.places.categories}'.split(',')}";
    private final PropertyService propertyService;
    private final CategoryLookupService categoryLookupService;
    private final ResponseMapperRegistry responseMapperRegistry;

    @Autowired
    public PropertiesController(PropertyService propertyService,
                                CategoryLookupService categoryLookupService,
                                ResponseMapperRegistry responseMapperRegistry) {
        this.propertyService = propertyService;
        this.categoryLookupService = categoryLookupService;
        this.responseMapperRegistry = responseMapperRegistry;
    }

    @PostMapping("/properties/search")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<PropertyResponse>> getProperties(
            @RequestBody FilterRequest filters,
            Pageable pageable) {
        Page<Property> propertiesPage = propertyService.searchPropertiesWithFilters(filters, pageable);
        Page<PropertyResponse> responsePage = propertiesPage.map(responseMapperRegistry::map);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/properties/details/{id}")
    public ResponseEntity<PropertyResponse> getPropertyDetail(@PathVariable("id") long id) {
        PropertyResponse p = responseMapperRegistry.map(propertyService.getProperty(id));
        if (p == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(p);
    }

    @GetMapping("/properties/featured")
    public ResponseEntity<Object> getFeatured() {
        return ResponseEntity.ok(propertyService.getFeatured().stream().map(responseMapperRegistry::map).toList());
    }

    /**
     * Endpoint per la creazione di una nuova proprietà con immagini.
     * Il client invia i dati della proprietà come JSON e le immagini come file multipart.
     */
    @PreAuthorize("@securityUtil.isAgentOrManager(authentication.principal, authentication.principal.id)")
    @PostMapping(value = "/properties", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<PropertyResponse> createProperty(
            @RequestPart("property") @Valid CreatePropertyRequest request,
            @RequestPart("images") List<MultipartFile> images,
            Authentication authentication) {
        logger.debug("POST /properties - request: {}", request);
        logger.debug("Numero immagini ricevute: {}", images.size());
        logger.debug("Dettagli CreatePropertyRequest: {}", request);
        logger.debug("Category name: {}", request.getPropertyCategoryName());
        logger.debug("Property type: {}", request.getPropertyType().name());
        PropertyResponse created = propertyService.createPropertyWithImages(request, images);
        logger.debug("Property creata id={}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Get available property types for the first dropdown.
     * Returns unique property types from active categories.
     * Endpoint: GET /api/property-types
     */
    @GetMapping("/api/property-types")
    public ResponseEntity<List<String>> getPropertyTypes() {
        logger.debug("Richiesta tipi di proprietà");
        List<String> propertyTypes = categoryLookupService.findDistinctActivePropertyTypes();
        List<String> sortedTypes = propertyTypes.stream().sorted().toList();
        logger.debug("Tipi di proprietà trovati: {}", sortedTypes);
        return ResponseEntity.ok(sortedTypes);
    }

    /**
     * Get categories for a specific property type for the second dropdown.
     * Endpoint: GET /api/categories?type=RESIDENTIAL
     */
    @GetMapping("/api/categories")
    public ResponseEntity<List<String>> getCategoriesByType(@RequestParam("type") String propertyType) {
        logger.debug("Richiesta categorie per tipo: {}", propertyType);
        List<PropertyCategory> categories = categoryLookupService.findByPropertyType(propertyType);
        List<String> categoryNames = categories.stream()
            .map(PropertyCategory::getName)
            .sorted()
            .toList();
        logger.debug("Categorie trovate per tipo {}: {}", propertyType, categoryNames);
        return ResponseEntity.ok(categoryNames);
    }

    /**
     * Endpoint per la ricerca di punti di interesse nelle vicinanze di un immobile.
     *
     * @param id ID dell'immobile di riferimento
     * @param radius raggio di ricerca in metri (default: 5000)
     * @param categories categorie di luoghi da cercare (default: ["commercial", "catering", "education"])
     * @return lista di luoghi trovati nelle vicinanze
     */
    @GetMapping("/api/properties/{id}/places")
    public ResponseEntity<List<PlaceDTO>> getNearbyPlaces(
            @PathVariable("id") Long id,
            @RequestParam(value = "radius", defaultValue = DEFAULT_RADIUS_SPEL) int radius,
            @RequestParam(value = "categories", defaultValue = DEFAULT_CATEGORIES_SPEL) List<String> categories) {
 
        logger.debug("Richiesta luoghi vicini per propertyId={}, radius={}, categories={}", id, radius, categories);
 
        // Il PropertyService dovrà ottenere le coordinate e chiamare il PlacesService
        List<PlaceDTO> places = propertyService.findNearbyPlaces(id, radius, categories);
 
        return ResponseEntity.ok(places);
    }

    // take agentID from principal
    @GetMapping("/api/properties/agent_properties/")
    @PreAuthorize("@securityUtil.canViewAgentRelatedEntities(principal, principal.id)")
    public ResponseEntity<Page<PropertyResponse>> getAgentProperties(@AuthenticationPrincipal AppPrincipal principal, Pageable pageable) {
        Long agentID = principal.getId();
        Page<Property> properties = propertyService.getPropertiesByAgentId(agentID, pageable);
        Page<PropertyResponse> response = properties.map(responseMapperRegistry::map);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/properties/{id}")
    @PreAuthorize("@securityUtil.canAccessProperty(authentication.principal, #id)")
    public ResponseEntity<Void> deleteProperty(@PathVariable("id") Long id, Authentication authentication) {
        Property property = propertyService.getProperty(id);
        if (property == null) {
            return ResponseEntity.notFound().build();
        }
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint per recuperare la cronologia degli immobili.
     * Accetta una lista di ID immobili e restituisce i dettagli completi.
     *
     * @param request DTO contenente la lista degli ID immobili da recuperare
     * @return lista di PropertyResponse con i dettagli degli immobili richiesti
     */
    @PostMapping("/api/properties/history")
    public ResponseEntity<List<PropertyResponse>> getPropertyHistory(
            @Valid @RequestBody PropertyHistoryRequest request) {
        logger.debug("POST /api/properties/history - request: {}", request);
        List<PropertyResponse> response = propertyService.getPropertyHistory(request);
        return ResponseEntity.ok(response);
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