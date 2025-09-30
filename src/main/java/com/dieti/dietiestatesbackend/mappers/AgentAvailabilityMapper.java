package com.dieti.dietiestatesbackend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.dieti.dietiestatesbackend.dto.request.AgentAvailabilityRequestDTO;
import com.dieti.dietiestatesbackend.dto.response.AgentAvailabilityResponseDTO;
import com.dieti.dietiestatesbackend.entities.AgentAvailability;
import com.dieti.dietiestatesbackend.entities.User;

/**
 * MapStruct mapper per AgentAvailability.
 * - Mappa Entity -> Response tramite MapStruct.
 * - Fornisce un metodo di fallback manuale per Request -> Entity per creare l'oggetto User nidificato.
 */
@Mapper(componentModel = "spring")
public interface AgentAvailabilityMapper {

    @Mapping(source = "agent.id", target = "agentId")
    AgentAvailabilityResponseDTO toResponse(AgentAvailability availability);

    default AgentAvailability toEntity(AgentAvailabilityRequestDTO dto) {
        if (dto == null) return null;
        AgentAvailability a = new AgentAvailability();
        User u = new User();
        u.setId(dto.getAgentId());
        a.setAgent(u);
        a.setStartTime(dto.getStartTime());
        a.setEndTime(dto.getEndTime());
        return a;
    }
}