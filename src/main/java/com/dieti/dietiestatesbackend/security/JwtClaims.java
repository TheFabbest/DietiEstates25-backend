package com.dieti.dietiestatesbackend.security;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO per mappare i claims del payload JWT.
 * Contiene inoltre i nomi standardizzati dei claim come costanti pubbliche
 * per evitare "magic strings" nel codice.
 */
public class JwtClaims {

    // Costanti per i nomi dei claim (evitano magic strings)
    public static final String CLAIM_ID = "id";
    public static final String CLAIM_IS_MANAGER = "isManager";
    public static final String CLAIM_ROLES = "roles";

    @JsonProperty(CLAIM_ID)
    private Long id;

    @JsonProperty(CLAIM_IS_MANAGER)
    private Boolean isManager;

    @JsonProperty(CLAIM_ROLES)
    private List<String> roles;

    public JwtClaims() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsManager() {
        return isManager;
    }

    public void setIsManager(Boolean isManager) {
        this.isManager = isManager;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}