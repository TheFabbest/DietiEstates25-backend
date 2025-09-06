package com.dieti.dietiestatesbackend.mappers.adapters;

import com.dieti.dietiestatesbackend.dto.request.CreateResidentialPropertyRequest;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.mappers.PropertyCreationMapper;
import com.dieti.dietiestatesbackend.mappers.RequestSubtypeMapper;
import org.springframework.stereotype.Component;

@Component
public class ResidentialPropertyMapperAdapter implements RequestSubtypeMapper<CreateResidentialPropertyRequest> {

    private final PropertyCreationMapper propertyCreationMapper;

    public ResidentialPropertyMapperAdapter(PropertyCreationMapper propertyCreationMapper) {
        this.propertyCreationMapper = propertyCreationMapper;
    }

    @Override
    public Property map(CreateResidentialPropertyRequest request, User agent) {
        return propertyCreationMapper.toEntity(request, agent);
    }

    @Override
    public Class<CreateResidentialPropertyRequest> getRequestType() {
        return CreateResidentialPropertyRequest.class;
    }
}