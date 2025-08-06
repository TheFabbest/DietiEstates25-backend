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
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "autorimessa")
public class Autorimessa extends BaseEntity {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id", foreignKey = @ForeignKey(name = "fk_autorimessa_immobile"))
    private Property immobile;

    @Column(name = "ha_sorveglianza")
    private boolean haSorveglianza = false;

    @Min(1)
    @Column(name = "numero_piani")
    private Integer numeroPiani = 1;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Property getImmobile() { return immobile; }
    public void setImmobile(Property immobile) { this.immobile = immobile; }

    public boolean isHaSorveglianza() { return haSorveglianza; }
    public void setHaSorveglianza(boolean haSorveglianza) { this.haSorveglianza = haSorveglianza; }

    public Integer getNumeroPiani() { return numeroPiani; }
    public void setNumeroPiani(Integer numeroPiani) { this.numeroPiani = numeroPiani; }
}