package com.dieti.dietiestatesbackend.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponseDTO {

    private Long id;

    private String country;

    private String province;

    private String city;

    private String street;

    private String streetNumber;

    private String building;

    private BigDecimal latitude;

    private BigDecimal longitude;

}