package com.dieti.dietiestatesbackend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.dieti.dietiestatesbackend.entities.Address;
import com.dieti.dietiestatesbackend.dto.response.AddressResponseDTO;
import com.dieti.dietiestatesbackend.dto.request.AddressRequest;

/**
 * MapStruct mapper per convertire Address -> AddressResponseDTO.
 * Mappa esplicitamente street_number -> streetNumber e le coordinate.
 */
@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mappings({
        @Mapping(source = "streetNumber", target = "streetNumber"),
        @Mapping(source = "coordinates.latitude", target = "latitude"),
        @Mapping(source = "coordinates.longitude", target = "longitude")
    })
    AddressResponseDTO toDto(Address address);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "coordinates", ignore = true) // Le coordinate verranno popolate dal GeocodingService
    @Mapping(source = "streetNumber", target = "streetNumber")
    @Mapping(target = "createdAt", ignore = true)
    Address toEntity(AddressRequest addressRequest);
}