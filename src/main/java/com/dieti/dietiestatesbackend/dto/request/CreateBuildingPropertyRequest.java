package com.dieti.dietiestatesbackend.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public sealed abstract class CreateBuildingPropertyRequest extends AbstractCreatePropertyRequest implements CreatePropertyRequest
        permits CreateResidentialPropertyRequest, CreateCommercialPropertyRequest, CreateGaragePropertyRequest {

    private Integer numberOfFloors;
}