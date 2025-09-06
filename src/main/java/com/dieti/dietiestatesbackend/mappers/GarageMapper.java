package com.dieti.dietiestatesbackend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mappings;

import com.dieti.dietiestatesbackend.entities.Garage;
import com.dieti.dietiestatesbackend.dto.response.GarageResponse;

/**
 * MapStruct mapper per convertire Garage -> GarageResponse.
 * Eredita la configurazione di base e aggiunge i mapping specifici per Garage.
 */
@Mapper(componentModel = "spring", uses = { MapStructPropertyMapper.class, AgentMapper.class, AddressMapper.class })
public interface GarageMapper {

    @InheritConfiguration(name = "propertyToPropertyResponse")
    @Mappings({
        // I campi comuni sono ora ereditati implicitamente.
        // Campi specifici
        @Mapping(target = "floors", expression = "java(property.getNumberOfFloors() == null ? null : java.util.List.of(String.valueOf(property.getNumberOfFloors())))"),
        @Mapping(target = "hasSurveillance", source = "hasSurveillance"),
        @Mapping(target = "numberOfFloors", source = "numberOfFloors")
    })
    GarageResponse toResponse(Garage property);


}