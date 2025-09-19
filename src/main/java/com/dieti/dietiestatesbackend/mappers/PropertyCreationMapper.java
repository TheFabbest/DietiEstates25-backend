package com.dieti.dietiestatesbackend.mappers;
import com.dieti.dietiestatesbackend.dto.request.AddressRequest;
import com.dieti.dietiestatesbackend.entities.Address;
import org.mapstruct.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.dieti.dietiestatesbackend.service.AddressService;
 
import com.dieti.dietiestatesbackend.dto.request.CreateCommercialPropertyRequest;
import com.dieti.dietiestatesbackend.dto.request.CreateGaragePropertyRequest;
import com.dieti.dietiestatesbackend.dto.request.CreateLandPropertyRequest;
import com.dieti.dietiestatesbackend.dto.request.CreateResidentialPropertyRequest;
import com.dieti.dietiestatesbackend.entities.CommercialProperty;
import com.dieti.dietiestatesbackend.entities.Garage;
import com.dieti.dietiestatesbackend.entities.Land;
import com.dieti.dietiestatesbackend.entities.ResidentialProperty;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.entities.Contract;
import com.dieti.dietiestatesbackend.entities.PropertyCategory;
import com.dieti.dietiestatesbackend.exception.EntityNotFoundException;
import com.dieti.dietiestatesbackend.service.lookup.AgentLookupService;
import com.dieti.dietiestatesbackend.service.lookup.CategoryLookupService;
import com.dieti.dietiestatesbackend.service.lookup.ContractLookupService;
import com.dieti.dietiestatesbackend.service.lookup.HeatingLookupService;
 
/**
 * Mapper MapStruct per la creazione di Property dalle request.
 * Convertito in classe astratta per permettere l'iniezione dei servizi di lookup necessari
 * per risolvere i campi complessi (es. da String a Contract).
 */
@Mapper(componentModel = "spring", uses = {AgentLookupService.class, CategoryLookupService.class, ContractLookupService.class, HeatingLookupService.class, AddressMapper.class})
public abstract class PropertyCreationMapper {

    @Autowired
    protected ContractLookupService contractLookupService;

    @Autowired
    protected CategoryLookupService categoryLookupService;

    @Autowired
    protected AddressMapper addressMapper;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "agent", source = "agent")
    @Mapping(target = "contract", source = "request.contractType")
    @Mapping(target = "propertyCategory", source = "request.propertyCategoryName")
    @Mapping(target = "address", source = "request.addressRequest", qualifiedBy = AddressMapping.class)
    @Mapping(target = "heating", source = "request.heatingType")
    @Mapping(target = "numberOfFloors", source = "request.numberOfFloors")
    @Mapping(target = "yearBuilt", source = "request.yearBuilt")
    @Mapping(target = "floor", source = "request.floor") // Mappa il campo floor
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "additionalFeatures", ignore = true)
    @Mapping(target = "imageDirectoryUlid", ignore = true)
    @Mapping(target = "numberOfImages", ignore = true)
    public abstract ResidentialProperty toEntity(CreateResidentialPropertyRequest request, User agent);
 
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "agent", source = "agent")
    @Mapping(target = "contract", source = "request.contractType")
    @Mapping(target = "propertyCategory", source = "request.propertyCategoryName")
    @Mapping(target = "address", source = "request.addressRequest", qualifiedBy = AddressMapping.class)
    @Mapping(target = "hasWheelchairAccess", source = "request.hasDisabledAccess")
    @Mapping(target = "numeroVetrine", source = "request.shopWindowCount")
    @Mapping(target = "yearBuilt", source = "request.yearBuilt")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "additionalFeatures", ignore = true)
    @Mapping(target = "imageDirectoryUlid", ignore = true)
    @Mapping(target = "numberOfImages", ignore = true)
    public abstract CommercialProperty toEntity(CreateCommercialPropertyRequest request, User agent);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "agent", source = "agent")
    @Mapping(target = "contract", source = "request.contractType")
    @Mapping(target = "propertyCategory", source = "request.propertyCategoryName")
    @Mapping(target = "address", source = "request.addressRequest", qualifiedBy = AddressMapping.class)
    @Mapping(target = "accessibleFromStreet", source = "request.hasRoadAccess")
    @Mapping(target = "yearBuilt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "additionalFeatures", ignore = true)
    @Mapping(target = "imageDirectoryUlid", ignore = true)
    @Mapping(target = "numberOfImages", ignore = true)
    public abstract Land toEntity(CreateLandPropertyRequest request, User agent);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "agent", source = "agent")
    @Mapping(target = "contract", source = "request.contractType")
    @Mapping(target = "propertyCategory", source = "request.propertyCategoryName")
    @Mapping(target = "address", source = "request.addressRequest", qualifiedBy = AddressMapping.class)
    @Mapping(target = "yearBuilt", source = "request.yearBuilt")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "additionalFeatures", ignore = true)
    @Mapping(target = "imageDirectoryUlid", ignore = true)
    @Mapping(target = "numberOfImages", ignore = true)
    public abstract Garage toEntity(CreateGaragePropertyRequest request, User agent);
    

    /**
     * Metodo helper che MapStruct utilizza per mappare un nome di contratto (String) in un'entità Contract.
     * L'implementazione gestisce il caso nullo e lancia un'eccezione se il contratto non viene trovato.
     */
    protected Contract mapContract(String name) {
        if (name == null) return null;
        return contractLookupService.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Contract '" + name + "' not found"));
    }
    @Autowired
    protected AddressService addressService;

    @AddressMapping
    public Address mapAddress(AddressRequest addressRequest) {
        // Crea e geocodifica un'entità Address gestita tramite AddressService
        return addressService.createFromRequest(addressRequest);
    }

    @Qualifier
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.CLASS)
    public @interface AddressMapping {
    }
 
    /**
     * Metodo helper che MapStruct utilizza per mappare un nome di categoria (String) in un'entità PropertyCategory.
     * L'implementazione gestisce il caso nullo e lancia un'eccezione se la categoria non viene trovata.
     */
    protected PropertyCategory mapCategory(String name) {
        if (name == null) return null;
        return categoryLookupService.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("PropertyCategory '" + name + "' not found"));
    }
}