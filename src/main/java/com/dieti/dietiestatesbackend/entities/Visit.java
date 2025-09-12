package com.dieti.dietiestatesbackend.entities;

import java.time.LocalDate;

import com.dieti.dietiestatesbackend.enums.VisitStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Table(name = "visit")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Visit extends BaseEntity {

    @NotNull
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_property", nullable = false, foreignKey = @ForeignKey(name = "fk_offer_property"))
    private Property property;

    @NotNull
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false, foreignKey = @ForeignKey(name = "fk_offer_user"))
    private User user;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VisitStatus status = VisitStatus.PENDING;
}