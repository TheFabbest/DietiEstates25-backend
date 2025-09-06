package com.dieti.dietiestatesbackend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates {

    @Digits(integer = 10, fraction = 8)
    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Digits(integer = 11, fraction = 8)
    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;
}