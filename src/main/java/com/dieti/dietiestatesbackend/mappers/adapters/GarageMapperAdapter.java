package com.dieti.dietiestatesbackend.mappers.adapters;

import com.dieti.dietiestatesbackend.dto.request.CreateGaragePropertyRequest;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.mappers.PropertyCreationMapper;
import com.dieti.dietiestatesbackend.mappers.RequestSubtypeMapper;
import org.springframework.stereotype.Component;

@Component
public class GarageMapperAdapter implements RequestSubtypeMapper<CreateGaragePropertyRequest> {

    private final PropertyCreationMapper propertyCreationMapper;

    public GarageMapperAdapter(PropertyCreationMapper propertyCreationMapper) {
        this.propertyCreationMapper = propertyCreationMapper;
    }

    @Override
    public Property map(CreateGaragePropertyRequest request, User agent) {
        return propertyCreationMapper.toEntity(request, agent);
    }

    @Override
    public Class<CreateGaragePropertyRequest> getRequestType() {
        return CreateGaragePropertyRequest.class;
    }
}