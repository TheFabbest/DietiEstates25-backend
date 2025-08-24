package com.dieti.dietiestatesbackend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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

    // Costruttori, Getters e Setters
    public SignupRequest() {}

    public SignupRequest(String email, String username, String password, String name, String surname) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
}