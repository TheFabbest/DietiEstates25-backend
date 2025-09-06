package com.dieti.dietiestatesbackend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.InheritConfiguration;

import com.dieti.dietiestatesbackend.entities.Land;
import com.dieti.dietiestatesbackend.dto.response.LandResponse;

/**
 * MapStruct mapper per Land -> LandResponse.
 * Eredita la configurazione di base e aggiunge i mapping specifici per Land.
 */
@Mapper(componentModel = "spring", uses = { MapStructPropertyMapper.class, AddressMapper.class, AgentMapper.class })
public interface LandMapper {

    @InheritConfiguration(name = "propertyToPropertyResponse")
    @Mappings({
        // I campi comuni sono ora ereditati implicitamente.
        // Campi specifici
        @Mapping(target = "hasRoadAccess", source = "accessibleFromStreet")
    })
    LandResponse toResponse(Land property);

}