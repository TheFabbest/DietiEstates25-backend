package com.dieti.dietiestatesbackend.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.dieti.dietiestatesbackend.enums.StatoOfferta;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "offerta")
@SequenceGenerator(
    name = "offerta_seq",
    sequenceName = "dieti_estates.offerta_id_seq",
    allocationSize = 1
)
public class Offerta extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_immobile", nullable = false, foreignKey = @ForeignKey(name = "fk_offerta_immobile"))
    private Property immobile;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utente", nullable = false, foreignKey = @ForeignKey(name = "fk_offerta_utente"))
    private User utente;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(name = "prezzo", nullable = false, precision = 12, scale = 2)
    private BigDecimal prezzo;

    @NotNull
    @Column(name = "data", nullable = false)
    private LocalDate data;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "stato", nullable = false)
    private StatoOfferta stato = StatoOfferta.IN_ATTESA;

    // Getters and setters
    public Property getImmobile() { return immobile; }
    public void setImmobile(Property immobile) { this.immobile = immobile; }

    public User getUtente() { return utente; }
    public void setUtente(User utente) { this.utente = utente; }

    public BigDecimal getPrezzo() { return prezzo; }
    public void setPrezzo(BigDecimal prezzo) { this.prezzo = prezzo; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public StatoOfferta getStato() { return stato; }
    public void setStato(StatoOfferta stato) { this.stato = stato; }
}