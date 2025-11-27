package com.dieti.dietiestatesbackend.dto.request;

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
public class SignupRequest extends CreateUserRequest{
    @NotBlank(message = "La password è obbligatoria.")
    @Size(min = 8, message = "La password deve essere lunga almeno 8 caratteri.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).+$",
             message = "La password deve contenere almeno una lettera maiuscola, una lettera minuscola, un numero e un carattere speciale.")
    private String password;

    public SignupRequest(CreateUserRequest createUserRequest, String password) {
        super(createUserRequest.getEmail(), createUserRequest.getUsername(),
              createUserRequest.getName(), createUserRequest.getSurname());
        this.password = password;
    }
}