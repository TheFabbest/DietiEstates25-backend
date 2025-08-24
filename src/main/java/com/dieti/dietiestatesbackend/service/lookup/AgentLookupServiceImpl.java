package com.dieti.dietiestatesbackend.service.lookup;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.repositories.UserRepository;

/**
 * Implementazione semplice di AgentLookupService che incapsula la logica
 * di risoluzione dell'agente tramite username e verifica che l'utente sia un agente.
 */
@Service
public class AgentLookupServiceImpl implements AgentLookupService {

    private final UserRepository userRepository;

    @Autowired
    public AgentLookupServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findAgentByUsername(String username) {
        if (username == null) {
            return Optional.empty();
        }
        return userRepository.findByUsername(username)
                .filter(User::isAgent);
    }
}