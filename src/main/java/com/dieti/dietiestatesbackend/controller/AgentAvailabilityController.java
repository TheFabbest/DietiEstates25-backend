package com.dieti.dietiestatesbackend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dieti.dietiestatesbackend.dto.request.AgentAvailabilityRequestDTO;
import com.dieti.dietiestatesbackend.dto.response.AgentAvailabilityResponseDTO;
import com.dieti.dietiestatesbackend.entities.AgentAvailability;
import com.dieti.dietiestatesbackend.exception.AgentAvailabilityNotFoundException;
import com.dieti.dietiestatesbackend.mappers.AgentAvailabilityMapper;
import com.dieti.dietiestatesbackend.security.AppPrincipal;
import com.dieti.dietiestatesbackend.service.AgentAvailabilityService;

import jakarta.validation.Valid;

/**
 * Controller leggero per la gestione degli slot di disponibilità agente.
 * Il mapping tra DTO e Entity è delegato al mapper MapStruct.
 *
 * Autorizzazioni:
 * - creare: manager o lo stesso agente (@securityUtil.isAgentOrManager)
 * - listare/visualizzare: manager o agente interessato (@securityUtil.canViewAgentRelatedEntities)
 * - cancellare: manager o proprietario dello slot (@securityUtil.canManageAgentAvailability)
 */
@RestController
public class AgentAvailabilityController {

    private final AgentAvailabilityService availabilityService;
    private final AgentAvailabilityMapper mapper;

    @Autowired
    public AgentAvailabilityController(AgentAvailabilityService availabilityService,
                                       AgentAvailabilityMapper mapper) {
        this.availabilityService = availabilityService;
        this.mapper = mapper;
    }

    @PostMapping("/agent-availabilities")
    @PreAuthorize("@securityUtil.isAgentOrManager(authentication.principal, #request.agentId)")
    public ResponseEntity<AgentAvailabilityResponseDTO> createAvailability(
            @AuthenticationPrincipal AppPrincipal principal,
            @RequestBody @Valid AgentAvailabilityRequestDTO request) {

        AgentAvailability toCreate = mapper.toEntity(request);
        AgentAvailability created = availabilityService.create(toCreate);
        AgentAvailabilityResponseDTO response = mapper.toResponse(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/agents/{agentId}/availabilities")
    public ResponseEntity<List<AgentAvailabilityResponseDTO>> getAvailabilitiesForAgent(
            @PathVariable("agentId") Long agentId) {

        List<AgentAvailability> list = availabilityService.getAvailabilitiesForAgent(agentId);
        List<AgentAvailabilityResponseDTO> dto = list.stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/agent-availabilities/{id}")
    @PreAuthorize("@securityUtil.canManageAgentAvailability(authentication.principal, #id)")
    public ResponseEntity<AgentAvailabilityResponseDTO> getAvailabilityById(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal AppPrincipal principal) {
 
        AgentAvailability avail = availabilityService.findById(id)
                .orElseThrow(() -> new AgentAvailabilityNotFoundException(id));
 
        return ResponseEntity.ok(mapper.toResponse(avail));
    }

    @DeleteMapping("/agent-availabilities/{id}")
    @PreAuthorize("@securityUtil.canManageAgentAvailability(authentication.principal, #id)")
    public ResponseEntity<Void> deleteAvailability(@PathVariable("id") Long id) {
        availabilityService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}