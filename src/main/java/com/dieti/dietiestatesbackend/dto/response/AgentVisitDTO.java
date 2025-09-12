package com.dieti.dietiestatesbackend.dto.response;

import com.dieti.dietiestatesbackend.entities.Address;
import com.dieti.dietiestatesbackend.entities.Visit;

public class AgentVisitDTO {
    private Visit visit;
    private String propertyType;
    private String addressString;

    public AgentVisitDTO(Visit visit, String propertyType, Address address) {
        this.visit = visit;
        this.propertyType = propertyType;
        this.addressString = address != null ? address.toString() : null; // Call toString() here
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getAddressString() {
        return addressString;
    }

    public void setAddressString(String addressString) {
        this.addressString = addressString;
    }
}