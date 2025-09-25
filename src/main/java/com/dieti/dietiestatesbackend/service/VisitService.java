package com.dieti.dietiestatesbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import com.dieti.dietiestatesbackend.entities.Visit;
import com.dieti.dietiestatesbackend.repositories.VisitRepository;
import com.dieti.dietiestatesbackend.exception.EntityNotFoundException;
import com.dieti.dietiestatesbackend.dto.response.AgentVisitDTO;

@Service
@Transactional
public class VisitService {
    private static final Logger logger = LoggerFactory.getLogger(VisitService.class);

    private final VisitRepository visitRepository;

    @Autowired
    public VisitService(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    public Visit getVisit(Long id) {
        return visitRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Visit not found with id: " + id));
    }

    public Page<AgentVisitDTO> getAgentVisits(Long agentID, Pageable pageable) {
        return visitRepository.getAgentVisits(agentID, pageable);
    }
}