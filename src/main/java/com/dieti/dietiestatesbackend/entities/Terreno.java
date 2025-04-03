package com.dieti.dietiestatesbackend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "terreno", schema = "DietiEstates2025")
public class Terreno {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id", foreignKey = @ForeignKey(name = "fk_terreno_immobile"))
    private Immobile immobile;

    @Column(name = "ha_ingresso_dalla_strada")
    private boolean haIngressoDallaStrada = true;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Immobile getImmobile() { return immobile; }
    public void setImmobile(Immobile immobile) { this.immobile = immobile; }

    public boolean haIngressoDallaStrada() { return haIngressoDallaStrada; }
    public void setHaIngressoDallaStrada(boolean haIngressoDallaStrada) { this.haIngressoDallaStrada = haIngressoDallaStrada; }
}