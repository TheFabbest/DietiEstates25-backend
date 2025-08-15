package com.dieti.dietiestatesbackend.service;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dieti.dietiestatesbackend.entities.Visit;
import com.dieti.dietiestatesbackend.repositories.VisitRepository;

@Service
@Transactional
public class VisitService {
    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    private final VisitRepository visitRepository;

    @Autowired
    public VisitService(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    public Visit getVisit(Long id) throws SQLException {
        return visitRepository.findById(id).get();
    }

    public List<Visit> getAgentVisits(Long agentId) {
        return visitRepository.getAgentVisits(agentId);
    }
}