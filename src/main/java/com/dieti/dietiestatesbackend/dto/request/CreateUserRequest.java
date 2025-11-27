package com.dieti.dietiestatesbackend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    @NotBlank(message = "L'email è obbligatoria.")
    @Email(message = "L'email non è valida.")
    private String email;

    @NotBlank(message = "Lo username è obbligatorio.")
    private String username;

    @NotBlank(message = "Il nome è obbligatorio.")
    private String name;

    @NotBlank(message = "Il cognome è obbligatorio.")
    private String surname;
}
