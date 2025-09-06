package com.dieti.dietiestatesbackend.mappers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dieti.dietiestatesbackend.dto.response.PropertyResponse;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.Land;
import com.dieti.dietiestatesbackend.entities.ResidentialProperty;
import com.dieti.dietiestatesbackend.entities.Garage;
import com.dieti.dietiestatesbackend.entities.CommercialProperty;

/**
 * Registry type-safe (Facade) per il mapping delle Property in PropertyResponse.
 * Registra esplicitamente i mapper MapStruct per sottotipo e fornisce un metodo
 * pubblico map(Property) che nasconde i dettagli di dispatching.
 */
@Component
public class ResponseMapperRegistry {

    private final Map<Class<? extends Property>, Function<Property, PropertyResponse>> registry = new HashMap<>();
    private final MapStructPropertyMapper defaultMapper;

    @Autowired
    public ResponseMapperRegistry(LandMapper landMapper,
                                  ResidentialPropertyMapper residentialPropertyMapper,
                                  GarageMapper garageMapper,
                                  CommercialPropertyMapper commercialPropertyMapper,
                                  MapStructPropertyMapper defaultMapper) {
        this.defaultMapper = defaultMapper;

        // Registrazione esplicita dei mapper specifici
        registry.put(Land.class, p -> landMapper.toResponse((Land) p));
        registry.put(ResidentialProperty.class, p -> residentialPropertyMapper.toResponse((ResidentialProperty) p));
        registry.put(Garage.class, p -> garageMapper.toResponse((Garage) p));
        registry.put(CommercialProperty.class, p -> commercialPropertyMapper.toResponse((CommercialProperty) p));
    }

    /**
     * Seleziona e invoca il mapper più appropriato per il runtime-type di {@code property}.
     * Se non viene trovato un mapper specifico, delega al mapper di default MapStruct.
     */
    public PropertyResponse map(Property property) {
        if (property == null) {
            return null;
        }

        Function<Property, PropertyResponse> fn = registry.get(property.getClass());

        if (fn == null) {
            // Cerca un handler compatibile (es. per sottoclassi)
            for (Entry<Class<? extends Property>, Function<Property, PropertyResponse>> e : registry.entrySet()) {
                if (e.getKey().isInstance(property)) {
                    fn = e.getValue();
                    break;
                }
            }
        }

        if (fn != null) {
            return fn.apply(property);
        }

        // fallback al mapper generico MapStruct
        return defaultMapper.propertyToPropertyResponse(property);
    }
}