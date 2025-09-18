package com.dieti.dietiestatesbackend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dieti.dietiestatesbackend.dto.request.FilterRequest;
import com.dieti.dietiestatesbackend.entities.Property;

/**
 * Interfaccia per il servizio di query delle proprietà.
 * Segue il principio di Dependency Inversion, permettendo al controller
 * di dipendere da un'astrazione invece che da un'implementazione concreta.
 */
public interface PropertyQueryServiceInterface {
    
    /**
     * Cerca proprietà utilizzando filtri avanzati.
     * Include filtri geografici con approccio a due fasi (bounding box DB + Haversine in memoria).
     * I filtri geografici (centerLatitude, centerLongitude, radiusInMeters) sono ora obbligatori.
     *
     * @param filters filtri di ricerca
     * @return lista di proprietà che corrispondono ai filtri
     */
    Page<Property> searchPropertiesWithFilters(FilterRequest filters, Pageable pageable);
    
    /**
     * Ottiene le proprietà in evidenza (ultime 4).
     *
     * @return lista delle proprietà in evidenza
     */
    List<Property> getFeatured();
    
    /**
     * Ottiene una proprietà dettagliata per ID.
     *
     * @param propertyID ID della proprietà
     * @return la proprietà con l'ID specificato
     * @throws com.dieti.dietiestatesbackend.exception.EntityNotFoundException se la proprietà non viene trovata
     */
    Property getProperty(long propertyID);

    /**
     * Ottiene una lista di proprietà in base agli ID specificati.
     * Restituisce solo le proprietà esistenti, ignorando gli ID non trovati.
     *
     * @param propertyIds lista degli ID delle proprietà da recuperare
     * @return lista delle proprietà trovate
     */
    List<Property> getPropertiesByIds(List<String> propertyIds);
}