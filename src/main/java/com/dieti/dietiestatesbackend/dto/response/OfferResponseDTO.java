package com.dieti.dietiestatesbackend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.dieti.dietiestatesbackend.enums.OfferStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO di risposta per le offerte.
 * Mantiene coerenza con PropertyResponse e altri DTO di risposta.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfferResponseDTO {
    private Long id;
    private PropertyResponse property;
    private UserResponse user;
    private BigDecimal price;
    private LocalDate date;
    private OfferStatus status;
    private LocalDateTime createdAt;
}