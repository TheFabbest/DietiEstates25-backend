package com.dieti.dietiestatesbackend.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dieti.dietiestatesbackend.dto.Listing;
import com.dieti.dietiestatesbackend.dto.response.PropertyResponse;

@Service
public class PropertyService {
    
    private static final Logger logger = Logger.getLogger(PropertyService.class.getName());
    private final Connection myConnection;

    @Autowired
    public PropertyService(Connection myConnection) {
        this.myConnection = myConnection;
    }

    // // Common operations
    // Page<ImmobileResponse> getImmobili(Pageable pageable);
    public List<PropertyResponse> searchProperties(String keyword) throws SQLException {
        String query = "SELECT p.id, p.description, p.price, p.area, " +
                    "c.name AS contract_name, c.id AS contract_id, " +
                    "cat.name AS category_name, cat.id AS category_id, " +
                    "p.status, p.energy_rating, " +
                    "p.id_agent, a.full_name AS agent_name, " +
                    "p.id_address, addr.full_address AS address " +
                    "FROM dieti_estates.property p " +
                    "JOIN dieti_estates.contract c ON p.id_contract = c.id " +
                    "JOIN dieti_estates.property_category cat ON p.id_property_category = cat.id " +
                    "JOIN dieti_estates.user a ON p.id_agent = a.id " +
                    "JOIN dieti_estates.address addr ON p.id_address = addr.id " +
                    "WHERE p.description ILIKE ?";
        List<PropertyResponse> results = new ArrayList<>();
        try (PreparedStatement ps = myConnection.prepareStatement(query)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PropertyResponse response = new PropertyResponse();
                    response.setId(rs.getLong("id"));
                    response.setDescription(rs.getString("description"));
                    response.setPrice(rs.getBigDecimal("price"));
                    response.setArea(rs.getInt("area"));
                    response.setContract(rs.getString("contract_name"));
                    response.setPropertyCategory(rs.getString("category_name"));
                    response.setStatus(rs.getString("status"));
                    response.setEnergyClass(rs.getString("energy_rating"));
                    response.setId_agent(rs.getLong("id_agent"));
                    response.setId_address(rs.getLong("id_address"));
                    // TODO add images
                    results.add(response);
                }
            }
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
    public List<PropertyResponse> getFeatured() throws SQLException {
        String query = "SELECT * FROM dieti_estates.property WHERE id BETWEEN 1 AND 4";
        List<PropertyResponse> results = new ArrayList<>();
        PreparedStatement ps = myConnection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            PropertyResponse response = new PropertyResponse();
            response.setId(rs.getLong("id"));
            response.setDescription(rs.getString("description"));
            response.setPrice(rs.getBigDecimal("price"));
            response.setArea(rs.getInt("area"));
            response.setContract(rs.getString("contract_name"));
            response.setPropertyCategory(rs.getString("category_name"));
            response.setStatus(rs.getString("status"));
            response.setEnergyClass(rs.getString("energy_rating"));
            response.setId_agent(rs.getLong("id_agent"));
            response.setId_address(rs.getLong("id_address"));

            // TODO add images
            results.add(response);
        }
        return results;
    }
}