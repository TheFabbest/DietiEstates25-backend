package com.dieti.dietiestatesbackend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO di risposta per le proprietà.
 * Mantengo i campi esistenti ma rimuovo i getter/setter manuali
 * perché Lombok li genera automaticamente.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyResponse {
    private Long id;
    private String description;
    private BigDecimal price;
    private Integer area;
    private Integer yearBuilt;
    private String contract;
    private String propertyCategory;
    private String condition;
    private String energyRating;
    private AddressResponseDTO address;
    private AgentResponseDTO agent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String firstImageUrl;
    private int numberOfImages;
}