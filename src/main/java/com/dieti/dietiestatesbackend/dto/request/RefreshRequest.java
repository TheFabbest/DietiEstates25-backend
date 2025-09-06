package com.dieti.dietiestatesbackend.dto.request;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Simple DTO that carries a refresh token.
 * Converted to Lombok to remove boilerplate.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshRequest {

    @NotBlank(message = "Il refresh token è obbligatorio.")
    private String refreshToken;
}