package com.dieti.dietiestatesbackend.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.dieti.dietiestatesbackend.dto.request.CreateCommercialPropertyRequest;
import com.dieti.dietiestatesbackend.dto.request.CreateGaragePropertyRequest;
import com.dieti.dietiestatesbackend.dto.request.CreateLandPropertyRequest;
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
 * Usa il Visitor pattern e delega alla classe {@link ResponseBuildingVisitor}
 * la costruzione del sottotipo di PropertyResponse.
 */
@Component
public class PropertyMapper {

    public static PropertyResponse toResponse(Property property) {
        // Build type-specific response via external visitor
        ResponseBuildingVisitor visitor = new ResponseBuildingVisitor();
        property.accept(visitor);
        PropertyResponse response = visitor.getResponse();

        // common fields (apply to all response types)
        response.setId(property.getId());
        response.setDescription(property.getDescription());
        response.setPrice(property.getPrice());
        response.setArea(property.getArea());
        response.setYearBuilt(property.getYearBuilt());

        // Handle relationships safely
        if (property.getContract() != null) {
            response.setContract(property.getContract().getName());
        }

        if (property.getPropertyCategory() != null) {
            response.setPropertyCategory(property.getPropertyCategory().getCategory());
        }

        response.setStatus(property.getStatus() != null ? property.getStatus().toString() : null);
        response.setEnergyClass(property.getEnergyRating() != null ? property.getEnergyRating().toString() : null);

        if (property.getAgent() != null) {
            response.setAgent(property.getAgent());
        }

        if (property.getAddress() != null) {
            response.setId_address(property.getAddress().getId());
            response.setAddress(property.getAddress());
            response.setLatitude(property.getAddress().getLatitude());
            response.setLongitude(property.getAddress().getLongitude());
        }

        return response;
    }

    public static List<PropertyResponse> toResponseList(List<Property> properties) {
        return properties.stream()
                .map(PropertyMapper::toResponse)
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
        rp.setNumberOfFloors(req.getTotalFloors() == null ? 1 : req.getTotalFloors());
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
        cp.setNumberOfRooms(req.getNumeroLocali());
        cp.setFloor(req.getPiano());
        cp.setNumberOfBathrooms(req.getNumeroBagni());
        cp.setNumberOfFloors(req.getNumeroPianiTotali());
        cp.setHasWheelchairAccess(req.isHaAccessoDisabili());
        cp.setNumeroVetrine(req.getNumeroVetrine() == null ? 0 : req.getNumeroVetrine());
        return cp;
    }

    public static Land toLandEntity(CreateLandPropertyRequest req) {
        Land land = new Land();
        return land;
    }

    public static Garage toGarageEntity(CreateGaragePropertyRequest req) {
        Garage g = new Garage();
        return g;
    }
}