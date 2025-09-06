package com.dieti.dietiestatesbackend.mappers.adapters;

import com.dieti.dietiestatesbackend.dto.request.CreateCommercialPropertyRequest;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.mappers.PropertyCreationMapper;
import com.dieti.dietiestatesbackend.mappers.RequestSubtypeMapper;
import org.springframework.stereotype.Component;

@Component
public class CommercialPropertyMapperAdapter implements RequestSubtypeMapper<CreateCommercialPropertyRequest> {

    private final PropertyCreationMapper propertyCreationMapper;

    public CommercialPropertyMapperAdapter(PropertyCreationMapper propertyCreationMapper) {
        this.propertyCreationMapper = propertyCreationMapper;
    }

    @Override
    public Property map(CreateCommercialPropertyRequest request, User agent) {
        return propertyCreationMapper.toEntity(request, agent);
    }

    @Override
    public Class<CreateCommercialPropertyRequest> getRequestType() {
        return CreateCommercialPropertyRequest.class;
    }
}