package com.dieti.dietiestatesbackend.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.dieti.dietiestatesbackend.dto.request.CreatePropertyRequest;
import com.dieti.dietiestatesbackend.enums.PropertyType;

/**
 * Factory semplice che risolve il PropertyCreator corretto in base al PropertyType.
 */
@Component
public class PropertyCreatorFactory {

    private final Map<PropertyType, PropertyCreator<? extends CreatePropertyRequest>> creators;

    public PropertyCreatorFactory(List<PropertyCreator<? extends CreatePropertyRequest>> creatorList) {
        this.creators = creatorList.stream()
                .collect(Collectors.toMap(PropertyCreator::supports, c -> c));
    }

    public PropertyCreator<? extends CreatePropertyRequest> getCreator(PropertyType type) {
        return creators.get(type);
    }
}