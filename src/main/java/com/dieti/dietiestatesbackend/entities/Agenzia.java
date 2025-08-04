package com.dieti.dietiestatesbackend.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "agenzia")
@SequenceGenerator(
    name = "agenzia_seq",
    sequenceName = "dieti_estates.\"Agenzia_idagenzia_seq\"",
    allocationSize = 1
)
public class Agenzia extends BaseEntity {

    @NotBlank
    @Column(name = "nome", unique = true, nullable = false)
    private String nome;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_indirizzo", referencedColumnName = "id", nullable = false, foreignKey = @ForeignKey(name = "fk_agenzia_indirizzo"))
    private Indirizzo indirizzo;

    @OneToMany(mappedBy = "agenzia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Utente> utenti = new ArrayList<>();

    // Getters and setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Indirizzo getIndirizzo() { return indirizzo; }
    public void setIndirizzo(Indirizzo indirizzo) { this.indirizzo = indirizzo; }

    public List<Utente> getUtenti() { return utenti; }
    public void setUtenti(List<Utente> utenti) { this.utenti = utenti; }
}