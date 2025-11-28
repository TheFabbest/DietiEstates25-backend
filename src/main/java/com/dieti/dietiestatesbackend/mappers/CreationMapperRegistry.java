package com.dieti.dietiestatesbackend.mappers;

import com.dieti.dietiestatesbackend.dto.request.AbstractCreatePropertyRequest;
import com.dieti.dietiestatesbackend.dto.request.CreatePropertyRequest;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.User;
import com.dieti.dietiestatesbackend.exception.InvalidPayloadException;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class CreationMapperRegistry {

    private final Map<Class<? extends AbstractCreatePropertyRequest>, RequestSubtypeMapper<? extends AbstractCreatePropertyRequest>> mappersMap;

    public CreationMapperRegistry(List<RequestSubtypeMapper<? extends AbstractCreatePropertyRequest>> mappers) {
        Objects.requireNonNull(mappers, "mappers must not be null");
        this.mappersMap = new HashMap<>();
        for (RequestSubtypeMapper<? extends AbstractCreatePropertyRequest> mapper : mappers) {
            if (mapper != null && mapper.getRequestType() != null) {
                this.mappersMap.put(mapper.getRequestType(), mapper);
            }
        }
    }

    public Property map(CreatePropertyRequest request, User agent) {
        Objects.requireNonNull(request, "request must not be null");

        // try direct lookup by exact request class
        RequestSubtypeMapper<? extends AbstractCreatePropertyRequest> mapper =
                mappersMap.get(request.getClass());

        // fallback: find a mapper whose declared request type is a supertype of the actual request
        if (mapper == null) {
            mapper = mappersMap.entrySet().stream()
                    .filter(e -> e.getKey().isInstance(request))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(null);
        }

        if (mapper != null) {
            return mapWithCapturedMapper(mapper, (AbstractCreatePropertyRequest) request, agent);
        }

        throw new InvalidPayloadException(Map.of("propertyType",
                "No suitable mapper found for property type: " + request.getClass().getSimpleName()));
    }

    /**
     * Usa la capture conversion per eseguire il mapping in modo type-safe senza unchecked-cast né @SuppressWarnings.
     */
    private static <T extends AbstractCreatePropertyRequest> Property mapWithCapturedMapper(
            RequestSubtypeMapper<T> mapper,
            AbstractCreatePropertyRequest request,
            User agent) {
        // Class.cast effettua il controllo a runtime garantendo sicurezza
        T typedRequest = mapper.getRequestType().cast(request);
        return mapper.map(typedRequest, agent);
    }
}