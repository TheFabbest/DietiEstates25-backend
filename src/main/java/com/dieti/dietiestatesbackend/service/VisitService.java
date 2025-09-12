package com.dieti.dietiestatesbackend.service;
 
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
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

    public List<AgentVisitDTO> getAgentVisits(Long agentID) {
        return visitRepository.getAgentVisits(agentID);
    }
}