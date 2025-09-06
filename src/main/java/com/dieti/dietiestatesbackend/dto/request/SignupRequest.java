package com.dieti.dietiestatesbackend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO per la registrazione utente.
 * Mantengo le validazioni e lascio a Lombok la generazione di accessor/costruttori.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @NotBlank(message = "L'email è obbligatoria.")
    @Email(message = "L'email non è valida.")
    private String email;

    @NotBlank(message = "Lo username è obbligatorio.")
    private String username;

    @NotBlank(message = "La password è obbligatoria.")
    @Size(min = 8, message = "La password deve essere lunga almeno 8 caratteri.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).+$",
             message = "La password deve contenere almeno una lettera maiuscola, una lettera minuscola, un numero e un carattere speciale.")
    private String password;

    @NotBlank(message = "Il nome è obbligatorio.")
    private String name;

    @NotBlank(message = "Il cognome è obbligatorio.")
    private String surname;
}