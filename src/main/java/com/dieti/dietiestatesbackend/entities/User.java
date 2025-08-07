package com.dieti.dietiestatesbackend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "user")
public class User extends BaseEntity {
    
    @NotBlank
    @Email
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @NotBlank
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @NotBlank
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "is_agent")
    private boolean isAgent = false;

    @Pattern(regexp = "^[A-Z]{2}\\d{6}$")
    @Column(name = "license")
    private String license;

    @Column(name = "is_manager")
    private boolean isManager = false;

    @ManyToOne
    @JoinColumn(name = "id_agency", foreignKey = @ForeignKey(name = "fk_user_agency"))
    private Agency agency;

    // Getters and setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String name) { this.firstName = name; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public boolean isAgent() { return isAgent; }
    public void setAgent(boolean isAgent) { this.isAgent = isAgent; }

    public String getLicense() { return license; }
    public void setLicense(String license) { this.license = license; }

    public boolean isManager() { return isManager; }
    public void setManager(boolean isManager) { this.isManager = isManager; }

    public Agency getAgency() { return agency; }
    public void setAgency(Agency agenzia) { this.agency = agenzia; }
}