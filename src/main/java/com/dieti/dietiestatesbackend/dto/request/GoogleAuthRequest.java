package com.dieti.dietiestatesbackend.dto.request;

import jakarta.validation.constraints.NotBlank;

public class GoogleAuthRequest {

    @NotBlank(message = "Il token Google è obbligatorio.")
    private String token;

    private String username;
    private String name;
    private String surname;

    // Costruttori, Getters e Setters
    public GoogleAuthRequest() {}

    public GoogleAuthRequest(String token) {
        this.token = token;
    }

    public GoogleAuthRequest(String token, String username, String name, String surname) {
        this.token = token;
        this.username = username;
        this.name = name;
        this.surname = surname;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
}