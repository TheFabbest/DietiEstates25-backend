package com.dieti.dietiestatesbackend.controller;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.dieti.dietiestatesbackend.dto.request.VisitCreationRequestDTO;
import com.dieti.dietiestatesbackend.dto.request.VisitStatusUpdateRequestDTO;
import com.dieti.dietiestatesbackend.dto.response.AgentVisitDTO;
import com.dieti.dietiestatesbackend.service.VisitService;
import com.dieti.dietiestatesbackend.security.AppPrincipal;
 
import jakarta.validation.Valid;
 
@RestController
public class VisitController {
    private final VisitService visitService;
 
    @Autowired
    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }
 
    @GetMapping("/visits/agent/{agentID}")
    @PreAuthorize("@securityUtil.canViewAgentRelatedEntities(authentication.principal, #agentID)")
    public ResponseEntity<Page<AgentVisitDTO>> getAgentVisits(@PathVariable("agentID") Long agentID, Pageable pageable) {
        Page<AgentVisitDTO> visits = visitService.getAgentVisits(agentID, pageable);
        return ResponseEntity.ok(visits);
    }
 
    @GetMapping("visits/me/")
    public ResponseEntity<Page<AgentVisitDTO>> getMyVisits(@AuthenticationPrincipal AppPrincipal principal, Pageable pageable) {
        Page<AgentVisitDTO> visits = visitService.getUserVisits(principal.getId(), pageable);
        return ResponseEntity.ok(visits);
    }
 
    @GetMapping("/properties/{propertyId}/visits")
    @PreAuthorize("@securityUtil.canViewPropertyVisits(authentication.principal, #propertyId)")
    public ResponseEntity<Page<AgentVisitDTO>> getPropertyVisits(@PathVariable("propertyId") Long propertyId, Pageable pageable) {
        Page<AgentVisitDTO> visits = visitService.getPropertyVisits(propertyId, pageable);
        return ResponseEntity.ok(visits);
    }
 
    @PostMapping("/visits")
    @PreAuthorize("@securityUtil.canCreateVisit(authentication.principal, #visitRequest)")
    public ResponseEntity<AgentVisitDTO> createVisit(@AuthenticationPrincipal AppPrincipal principal,
                                                     @RequestBody @Valid VisitCreationRequestDTO visitRequest) {
        AgentVisitDTO createdVisit = visitService.createVisit(visitRequest, principal.getId());
        return ResponseEntity.ok(createdVisit);
    }
 
    @PutMapping("/visits/{visitId}/status")
    @PreAuthorize("@securityUtil.canUpdateVisitStatus(authentication.principal, #visitId, #statusRequest.status)")
    public ResponseEntity<AgentVisitDTO> updateVisitStatus(@PathVariable("visitId") Long visitId,
                                                           @RequestBody @Valid VisitStatusUpdateRequestDTO statusRequest) {
        AgentVisitDTO updated = visitService.updateVisitStatus(visitId, statusRequest.getStatus());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/visits/{visitId}")
    @PreAuthorize("@securityUtil.canCancelVisit(authentication.principal, #visitId)")
    public ResponseEntity<AgentVisitDTO> cancelVisit(@PathVariable("visitId") Long visitId,
                                                     @AuthenticationPrincipal AppPrincipal principal) {
        AgentVisitDTO result = visitService.cancelVisit(visitId, principal.getId());
        return ResponseEntity.ok(result);
    }
}