package com.dieti.dietiestatesbackend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.dieti.dietiestatesbackend.dto.response.HeatingDTO;
import com.dieti.dietiestatesbackend.entities.Heating;

/**
 * Mapper MapStruct per convertire Heating -> HeatingDTO.
 * Single Responsibility: mappa esclusivamente l'entità Heating nel DTO di primo livello.
 */
@Mapper(componentModel = "spring")
public interface HeatingMapper {

    @Mapping(source = "name", target = "type")
    HeatingDTO toDto(Heating heating);

}