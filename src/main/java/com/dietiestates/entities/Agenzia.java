package com.dietiestates.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "agenzia", schema = "DietiEstates2025")
@SequenceGenerator(
    name = "agenzia_seq",
    sequenceName = "DietiEstates2025.\"agenzia_id_seq\"",
    allocationSize = 1
)
public class Agenzia extends BaseEntity {

    @NotBlank
    @Column(name = "nome", unique = true, nullable = false)
    private String nome;

    @NotBlank
    @Column(name = "citta", nullable = false)
    private String citta;

    @NotBlank
    @Column(name = "via", nullable = false)
    private String via;

    @Column(name = "civico")
    private String civico;

    @Column(name = "edificio")
    private String edificio;

    @NotBlank
    @Column(name = "provincia", nullable = false)
    private String provincia;

    @OneToMany(mappedBy = "agenzia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Utente> utenti = new ArrayList<>();

    // Getters and setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCitta() { return citta; }
    public void setCitta(String citta) { this.citta = citta; }

    public String getVia() { return via; }
    public void setVia(String via) { this.via = via; }

    public String getCivico() { return civico; }
    public void setCivico(String civico) { this.civico = civico; }

    public String getEdificio() { return edificio; }
    public void setEdificio(String edificio) { this.edificio = edificio; }

    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }

    public List<Utente> getUtenti() { return utenti; }
    public void setUtenti(List<Utente> utenti) { this.utenti = utenti; }
}