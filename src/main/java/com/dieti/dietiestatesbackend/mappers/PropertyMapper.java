package com.dieti.dietiestatesbackend.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.dieti.dietiestatesbackend.dto.response.PropertyResponse;
import com.dieti.dietiestatesbackend.entities.Property;

@Component
public class PropertyMapper {
    
    public static PropertyResponse toResponse(Property property) {
        PropertyResponse response = new PropertyResponse();
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
        
        response.setStatus(property.getStatus().toString());
        response.setEnergyClass(property.getEnergyRating().toString());
        
        if (property.getAgent() != null) {
            response.setId_agent(property.getAgent().getId());
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
}