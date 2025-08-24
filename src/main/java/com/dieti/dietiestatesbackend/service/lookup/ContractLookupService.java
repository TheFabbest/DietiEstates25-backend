package com.dieti.dietiestatesbackend.service.lookup;

import com.dieti.dietiestatesbackend.entities.Contract;
import java.util.Optional;

/**
 * Service interface per il lookup dei contratti.
 */
public interface ContractLookupService {
    Optional<Contract> findByName(String name);
}