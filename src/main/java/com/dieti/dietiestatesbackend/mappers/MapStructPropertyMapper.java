package com.dieti.dietiestatesbackend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import com.dieti.dietiestatesbackend.dto.response.PropertyResponse;
import com.dieti.dietiestatesbackend.entities.Contract;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.PropertyCategory;

/**
 * Mapper centrale per i campi comuni delle Property.
 * Fornisce una configurazione di base ereditabile per il mapping da Property a PropertyResponse.
 * Questo approccio centralizza la logica di mapping comune, migliorando la manutenibilità e riducendo la duplicazione.
 */
@Mapper(componentModel = "spring", uses = { AgentMapper.class, AddressMapper.class, HeatingMapper.class })
public interface MapStructPropertyMapper {

    /**
     * Configurazione di base per il mapping da Property a PropertyResponse.
     * Questo metodo, annotato con @Named, funge da template ereditabile per i mapper di sottotipo.
     * Definisce come mappare tutti i campi comuni, che verranno applicati prima dei mapping specifici del sottotipo.
     */
    @Named("propertyToPropertyResponse")
    @Mappings({
        @Mapping(target = "id", source = "id"),
        @Mapping(target = "description", source = "description"),
        @Mapping(target = "price", source = "price"),
        @Mapping(target = "area", source = "area"),
        @Mapping(target = "yearBuilt", source = "yearBuilt"),
        @Mapping(target = "contract", source = "contract.name"),
        @Mapping(target = "propertyCategory", source = "propertyCategory.name"),
        @Mapping(target = "condition", source = "condition"),
        // 'energyRating' ora matcha implicitamente.
        // 'agent' e 'address' sono gestiti dai mapper in 'uses'.
        @Mapping(target = "agent", source = "agent"),
        @Mapping(target = "address", source = "address"),
        @Mapping(target = "createdAt", source = "createdAt"),
        @Mapping(target = "imageDirectoryUlid", source = "imageDirectoryUlid"),
        @Mapping(target = "numberOfImages", source = "numberOfImages")
    })
    PropertyResponse propertyToPropertyResponse(Property property);

    /**
     * Helper usati da MapStruct per convertire tipi complessi in String.
     * Questi non sono più strettamente necessari se si usa il source path diretto (es. "contract.name"),
     * ma possono essere mantenuti per chiarezza o per gestire logiche di conversione più complesse.
     */
    default String contractToString(Contract c) {
        return c == null ? null : c.getName();
    }

    default String propertyCategoryToString(PropertyCategory pc) {
        return pc == null ? null : pc.getName();
    }
}