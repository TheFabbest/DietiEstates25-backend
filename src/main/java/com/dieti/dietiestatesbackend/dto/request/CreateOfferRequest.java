package com.dieti.dietiestatesbackend.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOfferRequest {

    @NotNull(message = "L'ID della proprietà è obbligatorio")
    @Positive(message = "L'ID della proprietà deve essere un valore positivo")
    private Long propertyId;

    @NotNull(message = "Il prezzo è obbligatorio")
    @Positive(message = "Il prezzo deve essere un valore positivo")
    private Double price;
}