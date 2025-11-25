package com.dieti.dietiestatesbackend.mappers;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.dieti.dietiestatesbackend.dto.response.LandResponse;
import com.dieti.dietiestatesbackend.entities.Land;

/**
 * MapStruct mapper per Land -> LandResponse.
 * Eredita la configurazione di base e aggiunge i mapping specifici per Land.
 */
@Mapper(componentModel = "spring", uses = { MapStructPropertyMapper.class, AddressMapper.class, AgentMapper.class })
public interface LandMapper {

    @InheritConfiguration(name = "propertyToPropertyResponse")

    // I campi comuni sono ora ereditati implicitamente.
    // Campi specifici
    @Mapping(target = "hasRoadAccess", source = "accessibleFromStreet")
    @Mapping(target = "firstImageUrl", ignore = true)

    LandResponse toResponse(Land property);

}