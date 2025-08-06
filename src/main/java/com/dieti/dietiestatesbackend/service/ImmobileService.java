package com.dieti.dietiestatesbackend.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.dieti.dietiestatesbackend.dto.request.ImmobileRequest;
import com.dieti.dietiestatesbackend.dto.request.ImmobileResidenzialeRequest;
import com.dieti.dietiestatesbackend.dto.request.ImmobileCommercialeRequest;
import com.dieti.dietiestatesbackend.dto.request.TerrenoRequest;
import com.dieti.dietiestatesbackend.dto.Listing;
import com.dieti.dietiestatesbackend.dto.request.AutorimessaRequest;
import com.dieti.dietiestatesbackend.dto.response.ImmobileResponse;
import com.dieti.dietiestatesbackend.dto.response.ImmobileResidenzialeResponse;
import com.dieti.dietiestatesbackend.dto.response.ImmobileCommercialeResponse;
import com.dieti.dietiestatesbackend.dto.response.TerrenoResponse;
import com.dieti.dietiestatesbackend.dto.response.AutorimessaResponse;
import com.dieti.dietiestatesbackend.entities.Property;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ImmobileService {
    
    private static final Logger logger = Logger.getLogger(ImmobileService.class.getName());
    private final Connection myConnection;

    @Autowired
    public ImmobileService(Connection myConnection) {
        this.myConnection = myConnection;
    }

    // // Common operations
    // Page<ImmobileResponse> getImmobili(Pageable pageable);
    public List<ImmobileResponse> searchImmobili(String keyword) throws SQLException {
        String query = "SELECT * FROM dieti_estates.immobile WHERE descrizione LIKE ?";
        PreparedStatement ps = myConnection.prepareStatement(query);
        ps.setString(1, "%"+keyword+"%");
        ResultSet rs = ps.executeQuery();

        List<ImmobileResponse> results = new ArrayList<>();
        while (rs.next()) {
            ImmobileResponse response = new ImmobileResponse();
            response.setId(rs.getLong("id"));
            response.setDescrizione(rs.getString("descrizione"));
            response.setPrezzo(rs.getBigDecimal("prezzo"));
            response.setSuperficie(rs.getInt("superficie"));
            response.setAgenteImmobiliare(rs.getInt("id_agente_immobiliare"));
            response.setIndirizzo(rs.getInt("id_indirizzo"));
            response.setUltimaModifica(rs.getTimestamp("ultima_modifica").toLocalDateTime());
            response.setContratto(rs.getInt("id_contratto"));
            //response.setCaratteristicheAddizionali(rs.); // TODO see
            // response.setCategoriaImmobile(rs.getInt("categoria_immobile")); // TODO see id or String or enum ?
            // response.setStatoImmobile(rs.getInt("stato_immobile"));
            // response.setClasseEnergetica(rs.getInt("classe_energetica"));
            // response.setTipologiaProprieta(rs.getInt("tipologia_proprieta"));
            //response.setCreatedAt(null); // TODO see if needed

            // Set other fields as necessary
            results.add(response);
        }
        return results;
    }
    // ImmobileResponse getImmobile(Long id) throws EntityNotFoundException;
    // void deleteImmobile(Long id, String username);
    // Page<ImmobileResponse> getImmobiliByAgente(String username, Pageable pageable);

    // // Residential property operations
    // ImmobileResidenzialeResponse createResidenziale(ImmobileResidenzialeRequest request, String username);
    // ImmobileResidenzialeResponse updateResidenziale(Long id, ImmobileResidenzialeRequest request, String username);
    // ImmobileResidenzialeResponse getImmobileResidenziale(Long id) throws EntityNotFoundException;
    // Page<ImmobileResidenzialeResponse> searchImmobiliResidenziali(String keyword, Pageable pageable);
    
    // // Commercial property operations
    // ImmobileCommercialeResponse createCommerciale(ImmobileCommercialeRequest request, String username);
    // ImmobileCommercialeResponse updateCommerciale(Long id, ImmobileCommercialeRequest request, String username);
    // ImmobileCommercialeResponse getImmobileCommerciale(Long id) throws EntityNotFoundException;
    // Page<ImmobileCommercialeResponse> searchImmobiliCommerciali(String keyword, Pageable pageable);
    
    // // Land operations
    // TerrenoResponse createTerreno(TerrenoRequest request, String username);
    // TerrenoResponse updateTerreno(Long id, TerrenoRequest request, String username);
    // TerrenoResponse getTerreno(Long id) throws EntityNotFoundException;
    // Page<TerrenoResponse> searchTerreni(String keyword, Pageable pageable);
    
    // // Garage operations
    // AutorimessaResponse createAutorimessa(AutorimessaRequest request, String username);
    // AutorimessaResponse updateAutorimessa(Long id, AutorimessaRequest request, String username);
    // AutorimessaResponse getAutorimessa(Long id) throws EntityNotFoundException;
    // Page<AutorimessaResponse> searchAutorimesse(String keyword, Pageable pageable);

    // // Filter operations
    // Page<ImmobileResidenzialeResponse> filterResidenziali(
    //     Double minPrezzo, Double maxPrezzo,
    //     Integer minSuperficie, Integer maxSuperficie,
    //     Integer numeroBagni, Integer numeroLocali,
    //     Pageable pageable
    // );

    // Page<ImmobileCommercialeResponse> filterCommerciali(
    //     Double minPrezzo, Double maxPrezzo,
    //     Integer minSuperficie, Integer maxSuperficie,
    //     Integer numeroBagni, Integer numeroLocali,
    //     Boolean haAccessoDisabili, Integer numeroVetrine,
    //     Pageable pageable
    // );

    // Page<TerrenoResponse> filterTerreni(
    //     Double minPrezzo, Double maxPrezzo,
    //     Integer minSuperficie, Integer maxSuperficie,
    //     Boolean haIngressoDallaStrada,
    //     Pageable pageable
    // );

    // Page<AutorimessaResponse> filterAutorimesse(
    //     Double minPrezzo, Double maxPrezzo,
    //     Integer minSuperficie, Integer maxSuperficie,
    //     Integer numeroPiani, Boolean haSorveglianza,
    //     Pageable pageable
    // );

    // // Utility methods
    // boolean isOwner(Long immobileId, String username);
    // Immobile validateOwnership(Long immobileId, String username) throws EntityNotFoundException;

    // More specific methods
    public List<Listing> getFeatured() throws SQLException {
        String query = "SELECT * FROM dieti_estates.immobile WHERE id BETWEEN 1 AND 4";
        List<Listing> listings = new ArrayList<>();
        // PreparedStatement ps = myConnection.prepareStatement(query);
        // ResultSet rs = ps.executeQuery();
        // while (rs.next()) {
        //     listings.add(new Listing(
        //         rs.getLong("id"),
        //         rs.getString("name"),
        //         rs.getString("description"),
        //         rs.getString("location"),
        //         rs.getFloat("price")
        //     ));
        // }
        return listings;
    }
}