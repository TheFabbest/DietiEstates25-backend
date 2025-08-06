package com.dieti.dietiestatesbackend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "contract")
public class Contract extends BaseEntity {

    @NotBlank
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "is_active")
    private boolean isActive = true;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String nome) { this.name = nome; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean attivo) { isActive = attivo; }
}