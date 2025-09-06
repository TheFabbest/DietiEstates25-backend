package com.dieti.dietiestatesbackend.mappers;

import com.dieti.dietiestatesbackend.dto.request.AbstractCreatePropertyRequest;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.User;

/**
 * Interfaccia per mappare sottotipi di richieste di creazione proprietà ai corrispondenti tipi di proprietà.
 * Implementa un approccio polimorfico e type-safe per la creazione delle proprietà.
 * 
 * @param <T> il tipo specifico di AbstractCreatePropertyRequest da mappare
 */
public interface RequestSubtypeMapper<T extends AbstractCreatePropertyRequest> {
    
    /**
     * Mappa una richiesta di creazione proprietà a un'entità Property corrispondente.
     * 
     * @param request la richiesta di creazione proprietà
     * @param agent l'agente che sta creando la proprietà
     * @return l'entità Property mappata
     */
    Property map(T request, User agent);
    
    /**
     * Restituisce il tipo di richiesta specifico gestito da questo mapper.
     * Questo metodo permette un approccio type-safe senza bisogno di instanceof.
     *
     * @return la classe del tipo di richiesta gestita da questo mapper
     */
    Class<T> getRequestType();
}