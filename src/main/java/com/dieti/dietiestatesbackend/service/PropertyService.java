package com.dieti.dietiestatesbackend.service;
 
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
 

import com.dieti.dietiestatesbackend.dto.request.CreatePropertyRequest;

import com.dieti.dietiestatesbackend.dto.request.FilterRequest;
import com.dieti.dietiestatesbackend.dto.response.PropertyResponse;
import com.dieti.dietiestatesbackend.entities.Address;
import com.dieti.dietiestatesbackend.entities.Contract;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.PropertyCategory;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.exception.EntityNotFoundException;
import com.dieti.dietiestatesbackend.mappers.PropertyMapper;
import com.dieti.dietiestatesbackend.repositories.PropertyRepository;
import com.dieti.dietiestatesbackend.service.lookup.AgentLookupService;
import com.dieti.dietiestatesbackend.service.lookup.ContractLookupService;
import com.dieti.dietiestatesbackend.service.lookup.CategoryLookupService;
import com.dieti.dietiestatesbackend.specification.PropertySpecifications;
 
import jakarta.persistence.EntityManager;
import jakarta.validation.Validator;
 
@Service
@Transactional
public class PropertyService {
    
    private static final Logger logger = LoggerFactory.getLogger(PropertyService.class);
    private final PropertyRepository propertyRepository;
    private final AgentLookupService agentLookupService;
    private final ContractLookupService contractLookupService;
    private final CategoryLookupService categoryLookupService;
    private final AddressService addressService;
    private final EntityManager entityManager;
    private final Validator validator;
    private final PropertyCreatorFactory propertyCreatorFactory;
 
    @Autowired
    public PropertyService(PropertyRepository propertyRepository,
                           AgentLookupService agentLookupService,
                           ContractLookupService contractLookupService,
                           CategoryLookupService categoryLookupService,
                           AddressService addressService,
                           EntityManager entityManager,
                           Validator validator,
                           PropertyCreatorFactory propertyCreatorFactory) {
        this.propertyRepository = propertyRepository;
        this.agentLookupService = agentLookupService;
        this.contractLookupService = contractLookupService;
        this.categoryLookupService = categoryLookupService;
        this.addressService = addressService;
        this.entityManager = entityManager;
        this.validator = validator;
        this.propertyCreatorFactory = propertyCreatorFactory;
    }

    // Common operations
    public List<Property> searchProperties(String keyword) {
        return propertyRepository.findByDescriptionContainingIgnoreCase(keyword);
    }

    public List<Property> searchPropertiesWithFilters(String keyword, FilterRequest filters) {
        return propertyRepository.findAll(PropertySpecifications.withFilters(keyword, filters));
    }

    // More specific methods
    public List<Property> getFeatured() {
        Pageable pageable = PageRequest.of(0, 4);
        Page<Property> featuredPage = propertyRepository.getFeatured(pageable);
        return featuredPage.getContent();
    }

    public Property getProperty(long propertyID) {
        return propertyRepository.findById(propertyID)
            .orElseThrow(() -> new EntityNotFoundException("Property not found with id: " + propertyID));
    }

    /**
     * Crea una proprietà a partire dal DTO unificato.
     * Metodo semplificato: delega la risoluzione a helper privati e mantiene la transazione.
     */
    public PropertyResponse createProperty(CreatePropertyRequest request) {
        logger.debug("Inizio creazione proprietà per tipo: {}", request.getPropertyType());
        try {
            User agent = resolveAgent(request);
            Contract contract = resolveContract(request);
            PropertyCategory category = resolveCategory(request.getPropertyCategoryName());
            Address address = resolveAddress(request);

            PropertyCreator<CreatePropertyRequest> creator = propertyCreatorFactory.getCreator(request.getPropertyType());
            if (creator == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported property type: " + request.getPropertyType());
            }

            Property property = creator.create(request, agent, contract, category, address, entityManager, validator);

            PropertyMapper.applyCommonFields(request, property);
            if (contract != null) property.setContract(contract);
            if (category != null) property.setPropertyCategory(category);
            property.setAgent(agent);
            property.setAddress(address);

            Property saved = propertyRepository.save(property);
            logger.info("Property created id={}", saved.getId());
            return PropertyMapper.toResponse(saved);
        } catch (EntityNotFoundException e) {
            logger.warn("Entity not found during property creation for request type {}: {}", request.getPropertyType(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Property creation failed: " + e.getMessage(), e);        
        } catch (ResponseStatusException e) {
            logger.warn("Property creation failed with status {}: {}", e.getStatusCode(), e.getReason());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error creating property", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e);
        }
    }

    // --- Helper methods (extracted) ---
    private User resolveAgent(CreatePropertyRequest request) {
        if (request.getAgentUsername() == null || request.getAgentUsername().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "agentUsername is required");
        }
        return agentLookupService.findAgentByUsername(request.getAgentUsername())
                .orElseThrow(() -> new EntityNotFoundException("Agent not found with username: " + request.getAgentUsername()));
    }

    private Contract resolveContract(CreatePropertyRequest request) {
        if (request.getContractType() == null || request.getContractType().isBlank()) {
            return null;
        }
        return contractLookupService.findByName(request.getContractType())
                .orElseThrow(() -> new EntityNotFoundException("Contract not found with name: " + request.getContractType()));
    }

    private PropertyCategory resolveCategory(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            return null;
        }
        return categoryLookupService.findByNameOrSubcategory(categoryName)
                .orElseThrow(() -> new EntityNotFoundException("PropertyCategory not found with name: " + categoryName));
    }

    private Address resolveAddress(CreatePropertyRequest request) {
        if (request.getAddressId() != null) {
            return addressService.findById(request.getAddressId())
                    .orElseThrow(() -> new EntityNotFoundException("Address not found with id: " + request.getAddressId()));
        }
        if (request.getAddressRequest() != null) {
            return addressService.createFromRequest(request.getAddressRequest());
        }
        throw new EntityNotFoundException("No address provided for property creation");
    }

    // Helper methods removed — use dedicated utilities if needed.
}