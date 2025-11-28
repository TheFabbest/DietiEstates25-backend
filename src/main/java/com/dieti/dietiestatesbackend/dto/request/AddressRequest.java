package com.dieti.dietiestatesbackend.dto.request;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO per inviare un indirizzo inline nella richiesta di creazione proprietà.
 * Corrisponde a [`src/main/java/com/dieti/dietiestatesbackend/dto/request/IndirizzoRequest.java:1`]
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {

    @NotBlank
    private String country;

    @NotBlank
    private String province;

    @NotBlank
    private String city;

    @NotBlank
    private String street;

    @JsonProperty("streetNumber")
    private String streetNumber;

    private String building;

    private BigDecimal latitude;

    private BigDecimal longitude;
}