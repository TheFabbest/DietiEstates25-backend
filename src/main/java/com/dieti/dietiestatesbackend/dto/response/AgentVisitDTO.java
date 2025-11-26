package com.dieti.dietiestatesbackend.dto.response;

import com.dieti.dietiestatesbackend.entities.Visit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgentVisitDTO {
    private Visit visit;
    private String propertyType;
    private AddressResponseDTO address;
    private UserResponse userInfo;

    public AgentVisitDTO(Visit visit, String propertyType, AddressResponseDTO address) {
        this.visit = visit;
        this.propertyType = propertyType;
        this.address = address;
        this.userInfo = null;
    }
}