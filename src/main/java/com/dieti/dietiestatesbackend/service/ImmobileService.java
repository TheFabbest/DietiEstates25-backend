package com.dieti.dietiestatesbackend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dieti.dietiestatesbackend.dto.request.ImmobileRequest;
import com.dieti.dietiestatesbackend.dto.request.ImmobileResidenzialeRequest;
import com.dieti.dietiestatesbackend.dto.request.ImmobileCommercialeRequest;
import com.dieti.dietiestatesbackend.dto.request.TerrenoRequest;
import com.dieti.dietiestatesbackend.dto.request.AutorimessaRequest;
import com.dieti.dietiestatesbackend.dto.response.ImmobileResponse;
import com.dieti.dietiestatesbackend.dto.response.ImmobileResidenzialeResponse;
import com.dieti.dietiestatesbackend.dto.response.ImmobileCommercialeResponse;
import com.dieti.dietiestatesbackend.dto.response.TerrenoResponse;
import com.dieti.dietiestatesbackend.dto.response.AutorimessaResponse;
import com.dieti.dietiestatesbackend.entities.Immobile;
import jakarta.persistence.EntityNotFoundException;

public interface ImmobileService {
    
    // Common operations
    Page<ImmobileResponse> getImmobili(Pageable pageable);
    Page<ImmobileResponse> searchImmobili(String keyword, Pageable pageable);
    ImmobileResponse getImmobile(Long id) throws EntityNotFoundException;
    void deleteImmobile(Long id, String username);
    Page<ImmobileResponse> getImmobiliByAgente(String username, Pageable pageable);

    // Residential property operations
    ImmobileResidenzialeResponse createResidenziale(ImmobileResidenzialeRequest request, String username);
    ImmobileResidenzialeResponse updateResidenziale(Long id, ImmobileResidenzialeRequest request, String username);
    ImmobileResidenzialeResponse getImmobileResidenziale(Long id) throws EntityNotFoundException;
    Page<ImmobileResidenzialeResponse> searchImmobiliResidenziali(String keyword, Pageable pageable);
    
    // Commercial property operations
    ImmobileCommercialeResponse createCommerciale(ImmobileCommercialeRequest request, String username);
    ImmobileCommercialeResponse updateCommerciale(Long id, ImmobileCommercialeRequest request, String username);
    ImmobileCommercialeResponse getImmobileCommerciale(Long id) throws EntityNotFoundException;
    Page<ImmobileCommercialeResponse> searchImmobiliCommerciali(String keyword, Pageable pageable);
    
    // Land operations
    TerrenoResponse createTerreno(TerrenoRequest request, String username);
    TerrenoResponse updateTerreno(Long id, TerrenoRequest request, String username);
    TerrenoResponse getTerreno(Long id) throws EntityNotFoundException;
    Page<TerrenoResponse> searchTerreni(String keyword, Pageable pageable);
    
    // Garage operations
    AutorimessaResponse createAutorimessa(AutorimessaRequest request, String username);
    AutorimessaResponse updateAutorimessa(Long id, AutorimessaRequest request, String username);
    AutorimessaResponse getAutorimessa(Long id) throws EntityNotFoundException;
    Page<AutorimessaResponse> searchAutorimesse(String keyword, Pageable pageable);

    // Filter operations
    Page<ImmobileResidenzialeResponse> filterResidenziali(
        Double minPrezzo, Double maxPrezzo,
        Integer minSuperficie, Integer maxSuperficie,
        Integer numeroBagni, Integer numeroLocali,
        Pageable pageable
    );

    Page<ImmobileCommercialeResponse> filterCommerciali(
        Double minPrezzo, Double maxPrezzo,
        Integer minSuperficie, Integer maxSuperficie,
        Integer numeroBagni, Integer numeroLocali,
        Boolean haAccessoDisabili, Integer numeroVetrine,
        Pageable pageable
    );

    Page<TerrenoResponse> filterTerreni(
        Double minPrezzo, Double maxPrezzo,
        Integer minSuperficie, Integer maxSuperficie,
        Boolean haIngressoDallaStrada,
        Pageable pageable
    );

    Page<AutorimessaResponse> filterAutorimesse(
        Double minPrezzo, Double maxPrezzo,
        Integer minSuperficie, Integer maxSuperficie,
        Integer numeroPiani, Boolean haSorveglianza,
        Pageable pageable
    );

    // Utility methods
    boolean isOwner(Long immobileId, String username);
    Immobile validateOwnership(Long immobileId, String username) throws EntityNotFoundException;
}