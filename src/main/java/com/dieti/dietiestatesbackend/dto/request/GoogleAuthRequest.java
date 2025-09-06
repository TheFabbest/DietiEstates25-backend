package com.dieti.dietiestatesbackend.dto.request;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO per autenticazione Google.
 * Mantengo le annotazioni di validazione; Lombok genera costruttori e accessor.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleAuthRequest {

    @NotBlank(message = "Il token Google è obbligatorio.")
    private String token;

    private String username;
    private String name;
    private String surname;
}