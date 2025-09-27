package com.dieti.dietiestatesbackend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mappings;

import com.dieti.dietiestatesbackend.dto.response.CommercialPropertyResponse;
import com.dieti.dietiestatesbackend.entities.CommercialProperty;

/**
 * Mapper specifico per CommercialProperty.
 * Eredita la configurazione di base e aggiunge i mapping specifici.
 */
@Mapper(componentModel = "spring", uses = { MapStructPropertyMapper.class, AgentMapper.class, AddressMapper.class })
public interface CommercialPropertyMapper {
@InheritConfiguration(name = "propertyToPropertyResponse")
@Mappings({
    // I campi comuni sono ora ereditati implicitamente.
    // Campi specifici
    @Mapping(target = "floor", source = "floor"),
    @Mapping(target = "hasDisabledAccess", source = "hasWheelchairAccess"),
    @Mapping(target = "totalFloors", source = "numberOfFloors")
})
    CommercialPropertyResponse toResponse(CommercialProperty property);

}