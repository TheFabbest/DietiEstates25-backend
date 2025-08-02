package com.dieti.dietiestatesbackend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "riscaldamento")
@SequenceGenerator(
    name = "riscaldamento_seq",
    sequenceName = "DietiEstates2025.riscaldamento_id_seq",
    allocationSize = 1
)
//LOOK-UP TABLE
public class Riscaldamento extends BaseEntity {

    @NotBlank
    @Column(name = "nome", unique = true, nullable = false)
    private String nome;

    @Column(name = "is_attivo")
    private boolean isAttivo = true;

    // Getters and setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public boolean isAttivo() { return isAttivo; }
    public void setAttivo(boolean attivo) { isAttivo = attivo; }
}