package com.dieti.dietiestatesbackend.service.lookup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dieti.dietiestatesbackend.entities.Heating;
import com.dieti.dietiestatesbackend.exception.EntityNotFoundException;
import com.dieti.dietiestatesbackend.repositories.HeatingRepository;

/**
 * Servizio di lookup per Heating.
 * Espone il metodo lookup(String heatingType) usato da MapStruct per risolvere
 * il nome del tipo di riscaldamento nell'entità Heating.
 */
@Service
public class HeatingLookupService {

    private final HeatingRepository heatingRepository;

    @Autowired
    public HeatingLookupService(HeatingRepository heatingRepository) {
        this.heatingRepository = heatingRepository;
    }

    /**
     * Recupera l'entità Heating per nome.
     * @param heatingType nome (type/name) del riscaldamento richiesto
     * @return Heating entità trovata
     * @throws EntityNotFoundException se il tipo non è presente o il parametro è nullo/blank
     */
    public Heating lookup(String heatingType) {
        if (heatingType == null || heatingType.isBlank()) {
            throw new EntityNotFoundException("Heating type is null or blank");
        }
        return heatingRepository.findByName(heatingType)
                .orElseThrow(() -> new EntityNotFoundException("Heating type '" + heatingType + "' not found"));
    }
}