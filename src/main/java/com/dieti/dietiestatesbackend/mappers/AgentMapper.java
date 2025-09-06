package com.dieti.dietiestatesbackend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.dieti.dietiestatesbackend.dto.response.AgentResponseDTO;
import com.dieti.dietiestatesbackend.entities.User;

/**
 * MapStruct mapper per convertire un {@link User} (quando usato come agente)
 * in {@link AgentResponseDTO}.
 *
 * Estrae anche i campi dell'agenzia annidata: agencyId e agencyName.
 */
@Mapper(componentModel = "spring")
public interface AgentMapper {

    @Mapping(source = "agency.id", target = "agencyId")
    @Mapping(source = "agency.name", target = "agencyName")
    AgentResponseDTO toAgent(User user);

}