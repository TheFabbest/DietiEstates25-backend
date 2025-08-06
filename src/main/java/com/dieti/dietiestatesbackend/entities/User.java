package com.dieti.dietiestatesbackend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "utente")
@SequenceGenerator(
    name = "utente_seq",
    sequenceName = "dieti_estates.utente_id_seq",
    allocationSize = 1
)
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
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotBlank
    @Column(name = "cognome", nullable = false)
    private String cognome;

    @Column(name = "is_agente")
    private boolean isAgente = false;

    @Pattern(regexp = "^[A-Z]{2}\\d{6}$")
    @Column(name = "licenza")
    private String licenza;

    @Column(name = "is_gestore")
    private boolean isGestore = false;

    @ManyToOne
    @JoinColumn(name = "id_agenzia", foreignKey = @ForeignKey(name = "fk_utente_agenzia"))
    private Agenzia agenzia;

    // Getters and setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public boolean isAgente() { return isAgente; }
    public void setAgente(boolean agente) { isAgente = agente; }

    public String getLicenza() { return licenza; }
    public void setLicenza(String licenza) { this.licenza = licenza; }

    public boolean isGestore() { return isGestore; }
    public void setGestore(boolean gestore) { isGestore = gestore; }

    public Agenzia getAgenzia() { return agenzia; }
    public void setAgenzia(Agenzia agenzia) { this.agenzia = agenzia; }
}