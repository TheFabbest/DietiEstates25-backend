package com.dieti.dietiestatesbackend.entities;

import java.util.List;

import com.dieti.dietiestatesbackend.enums.Giardino;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "immobile_residenziale", schema = "DietiEstates2025")
public class ImmobileResidenziale {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id", foreignKey = @ForeignKey(name = "fk_residenziale_immobile"))
    private Immobile immobile;

    @NotNull
    @Min(1)
    @Column(name = "numero_locali", nullable = false)
    private Integer numeroLocali;

    @NotNull
    @Min(1)
    @Column(name = "numero_bagni", nullable = false)
    private Integer numeroBagni;

    @Min(0)
    @Column(name = "posti_auto")
    private Integer postiAuto = 0;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_riscaldamento", nullable = false, foreignKey = @ForeignKey(name = "fk_residenziale_riscaldamento"))
    private Riscaldamento riscaldamento;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "giardino", nullable = false)
    private Giardino giardino;

    @Column(name = "is_arredato")
    private boolean isArredato = false;

    @NotEmpty
    @ElementCollection
    @CollectionTable(name = "immobile_residenziale_piani", joinColumns = @JoinColumn(name = "immobile_id"))
    @Column(name = "piano", nullable = false)
    private List<String> piani;

    @NotNull
    @Min(1)
    @Column(name = "numero_piani_totali", nullable = false)
    private Integer numeroPianiTotali;

    @Column(name = "ha_ascensore")
    private boolean haAscensore = false;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Immobile getImmobile() { return immobile; }
    public void setImmobile(Immobile immobile) { this.immobile = immobile; }

    public Integer getNumeroLocali() { return numeroLocali; }
    public void setNumeroLocali(Integer numeroLocali) { this.numeroLocali = numeroLocali; }

    public Integer getNumeroBagni() { return numeroBagni; }
    public void setNumeroBagni(Integer numeroBagni) { this.numeroBagni = numeroBagni; }

    public Integer getPostiAuto() { return postiAuto; }
    public void setPostiAuto(Integer postiAuto) { this.postiAuto = postiAuto; }

    public Riscaldamento getRiscaldamento() { return riscaldamento; }
    public void setRiscaldamento(Riscaldamento riscaldamento) { this.riscaldamento = riscaldamento; }

    public Giardino getGiardino() { return giardino; }
    public void setGiardino(Giardino giardino) { this.giardino = giardino; }

    public boolean isArredato() { return isArredato; }
    public void setArredato(boolean arredato) { isArredato = arredato; }

    public List<String> getPiani() { return piani; }
    public void setPiani(List<String> piani) { this.piani = piani; }

    public Integer getNumeroPianiTotali() { return numeroPianiTotali; }
    public void setNumeroPianiTotali(Integer numeroPianiTotali) { this.numeroPianiTotali = numeroPianiTotali; }

    public boolean isHaAscensore() { return haAscensore; }
    public void setHaAscensore(boolean haAscensore) { this.haAscensore = haAscensore; }
}