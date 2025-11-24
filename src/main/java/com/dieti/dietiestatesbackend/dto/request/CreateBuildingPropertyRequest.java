package com.dieti.dietiestatesbackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract sealed class CreateBuildingPropertyRequest extends AbstractCreatePropertyRequest implements CreatePropertyRequest
        permits CreateResidentialPropertyRequest, CreateCommercialPropertyRequest, CreateGaragePropertyRequest {

    private Integer numberOfFloors;
}