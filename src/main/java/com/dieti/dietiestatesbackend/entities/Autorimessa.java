package com.dieti.dietiestatesbackend.entities;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "autorimessa", schema = "DietiEstates2025")
public class Autorimessa {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id", foreignKey = @ForeignKey(name = "fk_autorimessa_immobile"))
    private Immobile immobile;

    @Column(name = "ha_sorveglianza")
    private boolean haSorveglianza = false;

    @NotEmpty
    @ElementCollection
    @CollectionTable(name = "autorimessa_piani", joinColumns = @JoinColumn(name = "immobile_id"))
    @Column(name = "piano", nullable = false)
    private List<String> piani;

    @Min(1)
    @Column(name = "numero_piani")
    private Integer numeroPiani = 1;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Immobile getImmobile() { return immobile; }
    public void setImmobile(Immobile immobile) { this.immobile = immobile; }

    public boolean isHaSorveglianza() { return haSorveglianza; }
    public void setHaSorveglianza(boolean haSorveglianza) { this.haSorveglianza = haSorveglianza; }

    public List<String> getPiani() { return piani; }
    public void setPiani(List<String> piani) { this.piani = piani; }

    public Integer getNumeroPiani() { return numeroPiani; }
    public void setNumeroPiani(Integer numeroPiani) { this.numeroPiani = numeroPiani; }
}