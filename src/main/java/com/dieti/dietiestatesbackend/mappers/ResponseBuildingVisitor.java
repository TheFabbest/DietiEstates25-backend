package com.dieti.dietiestatesbackend.mappers;

import java.util.List;

import com.dieti.dietiestatesbackend.dto.response.CommercialPropertyResponse;
import com.dieti.dietiestatesbackend.dto.response.GarageResponse;
import com.dieti.dietiestatesbackend.dto.response.LandResponse;
import com.dieti.dietiestatesbackend.dto.response.PropertyResponse;
import com.dieti.dietiestatesbackend.dto.response.ResidentialPropertyResponse;
import com.dieti.dietiestatesbackend.entities.CommercialProperty;
import com.dieti.dietiestatesbackend.entities.Garage;
import com.dieti.dietiestatesbackend.entities.Land;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.PropertyVisitor;
import com.dieti.dietiestatesbackend.entities.ResidentialProperty;

/** Builds the correct PropertyResponse subtype using the PropertyVisitor callbacks. */
public class ResponseBuildingVisitor implements PropertyVisitor {
    private PropertyResponse response;

    public PropertyResponse getResponse() {
        if (response == null) {
            response = new PropertyResponse();
        }
        return response;
    }

    @Override
    public void visit(ResidentialProperty residentialProperty) {
        ResidentialPropertyResponse r = new ResidentialPropertyResponse();
        r.setNumberOfRooms(residentialProperty.getNumberOfRooms());
        r.setNumberOfBathrooms(residentialProperty.getNumberOfBathrooms());
        r.setParkingSpaces(residentialProperty.getParkingSpaces());
        r.setFurnished(residentialProperty.isFurnished());
        r.setGarden(residentialProperty.getGarden());
        r.setTotalFloors(residentialProperty.getNumberOfFloors());
        r.setHasElevator(residentialProperty.hasElevator());
        try {
            r.setFloors(List.of(String.valueOf(residentialProperty.getFloor())));
        } catch (Exception e) {
            r.setFloors(null);
        }
        this.response = r;
    }

    @Override
    public void visit(CommercialProperty commercialProperty) {
        CommercialPropertyResponse c = new CommercialPropertyResponse();
        c.setNumberOfRooms(commercialProperty.getNumberOfRooms());
        c.setNumberOfBathrooms(commercialProperty.getNumberOfBathrooms());
        c.setTotalFloors(commercialProperty.getNumberOfFloors());
        c.setHasDisabledAccess(commercialProperty.getHasWheelchairAccess());
        c.setShopWindowCount(commercialProperty.getNumeroVetrine());
        this.response = c;
    }

    @Override
    public void visit(Garage garage) {
        GarageResponse g = new GarageResponse();
        g.setHasSurveillance(garage.isHasSurveillance());
        try {
            g.setFloors(List.of(String.valueOf(garage.getNumberOfFloors())));
            g.setNumberOfFloors(garage.getNumberOfFloors());
        } catch (Exception e) {
            g.setFloors(null);
        }
        this.response = g;
    }

    @Override
    public void visit(Land land) {
        LandResponse lr = new LandResponse();
        lr.setHasRoadAccess(land.getAccessibleFromStreet());
        this.response = lr;
    }

    @Override
    public void visit(Property property) {
        this.response = new PropertyResponse();
    }
}