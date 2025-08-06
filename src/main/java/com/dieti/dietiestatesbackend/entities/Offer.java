package com.dieti.dietiestatesbackend.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.dieti.dietiestatesbackend.enums.OfferStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "offer")
public class Offer extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_property", nullable = false, foreignKey = @ForeignKey(name = "fk_offer_property"))
    private Property property;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false, foreignKey = @ForeignKey(name = "fk_offer_user"))
    private User user;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(name = "prezzo", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OfferStatus status = OfferStatus.IN_ATTESA;

    // Getters and setters
    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate data) { this.date = data; }

    public OfferStatus getStatus() { return status; }
    public void setStatus(OfferStatus status) { this.status = status; }
}