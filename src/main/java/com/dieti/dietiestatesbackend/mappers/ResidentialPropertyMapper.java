package com.dieti.dietiestatesbackend.mappers;
 
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.dieti.dietiestatesbackend.dto.response.ResidentialPropertyResponse;
import com.dieti.dietiestatesbackend.entities.ResidentialProperty;
 
/**
 * Mapper specifico per ResidentialProperty.
 * Eredita la configurazione di base da MapStructPropertyMapper e aggiunge i mapping specifici.
 */
@Mapper(componentModel = "spring", uses = { MapStructPropertyMapper.class, AgentMapper.class, AddressMapper.class, HeatingMapper.class })
public interface ResidentialPropertyMapper {
 
    @InheritConfiguration(name = "propertyToPropertyResponse")

    // I campi comuni sono ora ereditati implicitamente.
    // Campi specifici per ResidentialProperty.
    @Mapping(target = "numberOfRooms", source = "numberOfRooms")
    @Mapping(target = "numberOfBathrooms", source = "numberOfBathrooms")
    @Mapping(target = "parkingSpaces", source = "parkingSpaces")
    @Mapping(target = "heating", source = "heating") // heating gestito da HeatingMapper
    @Mapping(target = "garden", source = "garden")
    @Mapping(target = "furnished", source = "furnished")
    @Mapping(target = "floor", source = "floor")
    @Mapping(target = "hasElevator", source = "hasElevator")
    @Mapping(target = "numberOfFloors", source = "numberOfFloors")
    @Mapping(target = "firstImageUrl", ignore = true)

    ResidentialPropertyResponse toResponse(ResidentialProperty property);
 
}