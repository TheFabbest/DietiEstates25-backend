package com.dieti.dietiestatesbackend.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.dieti.dietiestatesbackend.dto.request.CreateCommercialPropertyRequest;
import com.dieti.dietiestatesbackend.dto.request.CreatePropertyRequest;
import com.dieti.dietiestatesbackend.dto.request.CreateResidentialPropertyRequest;
import com.dieti.dietiestatesbackend.dto.response.PropertyResponse;
import com.dieti.dietiestatesbackend.entities.CommercialProperty;
import com.dieti.dietiestatesbackend.entities.Garage;
import com.dieti.dietiestatesbackend.entities.Land;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.ResidentialProperty;

/**
 * Mapper semplice e deterministico per convertire tra entità e DTO.
 * Ora delega al ResponseMapperRegistry (MapStruct-backed) per il mapping di risposta.
 * Mantiene i helper statici per la creazione di entità da request.
 */
@Component
public class PropertyMapper {

    private final ResponseMapperRegistry responseMapperRegistry;

    public PropertyMapper(ResponseMapperRegistry responseMapperRegistry) {
        this.responseMapperRegistry = responseMapperRegistry;
    }

    /**
     * Mappa un'entità Property nel DTO di risposta usando il registry dei mapper.
     * Questo evita duplicazione di mapping e garantisce che i mapper MapStruct
     * gestiscano correttamente sottotipi e relazioni (agent, address, heating).
     */
    public PropertyResponse toResponse(Property property) {
        return responseMapperRegistry.map(property);
    }

    public List<PropertyResponse> toResponseList(List<Property> properties) {
        return properties.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // --- Mapping helpers from Create*Request -> entities (popolano solo campi locali, non risolvono lookup) ---

    public static void applyCommonFields(CreatePropertyRequest req, Property property) {
        property.setDescription(req.getDescription());
        property.setPrice(req.getPrice());
        property.setArea(req.getArea());
        property.setYearBuilt(req.getYearBuilt());
        if (req.getImages() != null) {
            property.setImages(req.getImages());
        }
        property.setStatus(req.getStatus());
        property.setEnergyRating(req.getEnergyRating());
    }

    public static ResidentialProperty toResidentialEntity(CreateResidentialPropertyRequest req) {
        ResidentialProperty rp = new ResidentialProperty();
        rp.setNumberOfRooms(req.getNumberOfRooms());
        rp.setNumberOfBathrooms(req.getNumberOfBathrooms());
        rp.setParkingSpaces(req.getParkingSpaces() == null ? 0 : req.getParkingSpaces());
        rp.setFurnished(req.isFurnished());
        rp.setGarden(req.getGarden());
        rp.setNumberOfFloors(req.getNumberOfFloors() == null ? 1 : req.getNumberOfFloors());
        rp.setHasElevator(req.hasElevator());
        if (req.getFloors() != null && !req.getFloors().isEmpty()) {
            try {
                rp.setFloor(Integer.parseInt(req.getFloors().get(0)));
            } catch (NumberFormatException e) {
                rp.setFloor(1);
            }
        } else {
            rp.setFloor(1);
        }
        return rp;
    }

    public static CommercialProperty toCommercialEntity(CreateCommercialPropertyRequest req) {
        CommercialProperty cp = new CommercialProperty();
        cp.setNumberOfRooms(req.getNumberOfRooms());
        cp.setFloor(req.getFloor());
        cp.setNumberOfBathrooms(req.getNumberOfBathrooms());
        cp.setNumberOfFloors(req.getNumberOfFloors());
        cp.setHasWheelchairAccess(req.isHasDisabledAccess());
        cp.setNumeroVetrine(req.getShopWindowCount() == null ? 0 : req.getShopWindowCount());
        return cp;
    }

    public static Land toLandEntity(CreatePropertyRequest req) {
        Land land = new Land();
        applyCommonFields(req, land);
        return land;
    }

    public static Garage toGarageEntity(CreatePropertyRequest req) {
        Garage garage = new Garage();
        applyCommonFields(req, garage);
        return garage;
    }
}