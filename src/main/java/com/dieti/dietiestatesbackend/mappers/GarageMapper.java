package com.dieti.dietiestatesbackend.mappers;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.dieti.dietiestatesbackend.dto.response.GarageResponse;
import com.dieti.dietiestatesbackend.entities.Garage;

/**
 * MapStruct mapper per convertire Garage -> GarageResponse.
 * Eredita la configurazione di base e aggiunge i mapping specifici per Garage.
 */
@Mapper(componentModel = "spring", uses = { MapStructPropertyMapper.class, AgentMapper.class, AddressMapper.class })
public interface GarageMapper {

    @InheritConfiguration(name = "propertyToPropertyResponse")

    // I campi comuni sono ora ereditati implicitamente.
    // Campi specifici
    @Mapping(target = "hasSurveillance", source = "hasSurveillance")
    @Mapping(target = "floor", source = "floor")
    @Mapping(target = "numberOfFloors", source = "numberOfFloors")
    @Mapping(target = "firstImageUrl", ignore = true)

    GarageResponse toResponse(Garage property);


}