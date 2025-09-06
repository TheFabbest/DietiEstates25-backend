package com.dieti.dietiestatesbackend.mappers.adapters;

import com.dieti.dietiestatesbackend.dto.request.CreateLandPropertyRequest;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.mappers.PropertyCreationMapper;
import com.dieti.dietiestatesbackend.mappers.RequestSubtypeMapper;
import org.springframework.stereotype.Component;

@Component
public class LandMapperAdapter implements RequestSubtypeMapper<CreateLandPropertyRequest> {

    private final PropertyCreationMapper propertyCreationMapper;

    public LandMapperAdapter(PropertyCreationMapper propertyCreationMapper) {
        this.propertyCreationMapper = propertyCreationMapper;
    }

    @Override
    public Property map(CreateLandPropertyRequest request, User agent) {
        return propertyCreationMapper.toEntity(request, agent);
    }

    @Override
    public Class<CreateLandPropertyRequest> getRequestType() {
        return CreateLandPropertyRequest.class;
    }
}