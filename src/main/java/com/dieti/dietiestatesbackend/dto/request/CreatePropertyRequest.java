package com.dieti.dietiestatesbackend.dto.request;

import java.math.BigDecimal;
import java.util.List;

import com.dieti.dietiestatesbackend.enums.EnergyRating;
import com.dieti.dietiestatesbackend.enums.PropertyCondition;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.AssertTrue;
import com.fasterxml.jackson.annotation.JsonSubTypes;


/**
 * Sealed interface usata per la deserializzazione polimorfica con Jackson.
 * Dichiara gli accessor comuni in modo che il codice che accetta CreatePropertyRequest
 * (mappers, servizi) compili correttamente.
 *
 * Ora distinguiamo tra richieste per "building" (più sottotipi interni)
 * e "land". Jackson deserializzerà BUILDING verso CreateBuildingPropertyRequest
 * che a sua volta permette i sottotipi concreti (residential, commercial, garage).
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "propertyType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = CreateResidentialPropertyRequest.class, name = "RESIDENTIAL"),
    @JsonSubTypes.Type(value = CreateCommercialPropertyRequest.class, name = "COMMERCIAL"),
    @JsonSubTypes.Type(value = CreateGaragePropertyRequest.class, name = "GARAGE"),
    @JsonSubTypes.Type(value = CreateLandPropertyRequest.class, name = "LAND")
})

public sealed interface CreatePropertyRequest
    permits CreateBuildingPropertyRequest, CreateLandPropertyRequest {
 
    // Accessor comuni
    String getDescription();
    BigDecimal getPrice();
    Integer getArea();
    default Integer getYearBuilt() { return null; } // può essere null per Land
    String getContractType();

    String getPropertyCategoryName();

    PropertyCondition getCondition();
    EnergyRating getEnergyRating();

    com.dieti.dietiestatesbackend.enums.PropertyType getPropertyType(); // Aggiunto

    AddressRequest getAddressRequest();

    @AssertTrue(message = "È obbligatorio fornire un indirizzo tramite addressRequest.")
    default boolean isAddressValid() {
        return getAddressRequest() != null;
    }

    List<String> getImages();
    
    @AssertTrue(message = "Il tipo di proprietà non corrisponde alla categoria specificata.")
    default boolean isPropertyTypeConsistentWithCategory() {
        // Questa validazione sarà gestita a livello di servizio, non qui direttamente.
        // Lasciamo qui il placeholder per chiarezza, ma la logica effettiva
        // dipenderà dall'accesso al CategoryLookupService.
        return true;
    }
}