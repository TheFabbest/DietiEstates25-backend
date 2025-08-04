package com.dieti.dietiestatesbackend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "categoria_immobile")
@SequenceGenerator(
    name = "categoria_immobile_seq",
    sequenceName = "dieti_estates.categoria_immobile_id_seq",
    allocationSize = 1
)
//LOOK-UP TABLE
public class CategoriaImmobile extends BaseEntity {

    @NotBlank
    @Column(name = "categoria", nullable = false)
    private String categoria;

    @NotBlank
    @Column(name = "sottocategoria", unique = true, nullable = false)
    private String sottocategoria;

    @Column(name = "is_attivo")
    private boolean isAttivo = true;

    // Getters and setters
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getSottocategoria() { return sottocategoria; }
    public void setSottocategoria(String sottocategoria) { this.sottocategoria = sottocategoria; }

    public boolean isAttivo() { return isAttivo; }
    public void setAttivo(boolean attivo) { isAttivo = attivo; }
}