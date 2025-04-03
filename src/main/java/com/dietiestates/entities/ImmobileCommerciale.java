package com.dietiestates.entities;

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
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "immobile_commerciale", schema = "DietiEstates2025")
public class ImmobileCommerciale {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id", foreignKey = @ForeignKey(name = "fk_commerciale_immobile"))
    private Immobile immobile;

    @NotNull
    @Min(1)
    @Column(name = "numero_locali", nullable = false)
    private Integer numeroLocali;

    @NotEmpty
    @ElementCollection
    @CollectionTable(name = "immobile_commerciale_piani", joinColumns = @JoinColumn(name = "immobile_id"))
    @Column(name = "piano", nullable = false)
    private List<String> piani;

    @NotNull
    @Min(1)
    @Column(name = "numero_bagni", nullable = false)
    private Integer numeroBagni;

    @NotNull
    @Min(1)
    @Column(name = "numero_piani_totali", nullable = false)
    private Integer numeroPianiTotali;

    @Column(name = "ha_accesso_disabili")
    private boolean haAccessoDisabili = false;

    @Min(0)
    @Column(name = "numero_vetrine")
    private Integer numeroVetrine = 0;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Immobile getImmobile() { return immobile; }
    public void setImmobile(Immobile immobile) { this.immobile = immobile; }

    public Integer getNumeroLocali() { return numeroLocali; }
    public void setNumeroLocali(Integer numeroLocali) { this.numeroLocali = numeroLocali; }

    public List<String> getPiani() { return piani; }
    public void setPiani(List<String> piani) { this.piani = piani; }

    public Integer getNumeroBagni() { return numeroBagni; }
    public void setNumeroBagni(Integer numeroBagni) { this.numeroBagni = numeroBagni; }

    public Integer getNumeroPianiTotali() { return numeroPianiTotali; }
    public void setNumeroPianiTotali(Integer numeroPianiTotali) { this.numeroPianiTotali = numeroPianiTotali; }

    public boolean isHaAccessoDisabili() { return haAccessoDisabili; }
    public void setHaAccessoDisabili(boolean haAccessoDisabili) { this.haAccessoDisabili = haAccessoDisabili; }

    public Integer getNumeroVetrine() { return numeroVetrine; }
    public void setNumeroVetrine(Integer numeroVetrine) { this.numeroVetrine = numeroVetrine; }
}