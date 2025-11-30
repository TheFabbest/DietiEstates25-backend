package com.dieti.dietiestatesbackend.entities;

import java.math.BigDecimal;

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
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Table(name = "offer", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_property", "id_user"}, name = "uk_offer_property_user")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Offer extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_property", nullable = false, foreignKey = @ForeignKey(name = "fk_offer_property"))
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = true, foreignKey = @ForeignKey(name = "fk_offer_user"))
    private User user;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OfferStatus status = OfferStatus.PENDING;

    @Version
    private Long version;
}