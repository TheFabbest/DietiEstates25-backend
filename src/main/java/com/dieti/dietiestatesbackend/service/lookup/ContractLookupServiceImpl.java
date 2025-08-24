package com.dieti.dietiestatesbackend.service.lookup;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dieti.dietiestatesbackend.entities.Contract;
import com.dieti.dietiestatesbackend.repositories.ContractRepository;

/**
 * Implementazione semplice per il lookup dei contratti tramite ContractRepository.
 */
@Service
public class ContractLookupServiceImpl implements ContractLookupService {

    private final ContractRepository contractRepository;

    @Autowired
    public ContractLookupServiceImpl(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    @Override
    public Optional<Contract> findByName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        return contractRepository.findByName(name);
    }
}