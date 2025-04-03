package com.dietiestates.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "indirizzo", schema = "DietiEstates2025")
@SequenceGenerator(
    name = "indirizzo_seq",
    sequenceName = "DietiEstates2025.indirizzo_id_seq",
    allocationSize = 1
)
public class Indirizzo extends BaseEntity {

    @NotBlank
    @Column(name = "paese", nullable = false)
    private String paese;

    @NotBlank
    @Column(name = "provincia", nullable = false)
    private String provincia;

    @NotBlank
    @Column(name = "citta", nullable = false)
    private String citta;

    @NotBlank
    @Column(name = "via", nullable = false)
    private String via;

    @Column(name = "civico")
    private String civico;

    @Column(name = "edificio")
    private String edificio;

    @NotNull
    @Digits(integer = 10, fraction = 8)
    @Column(name = "latitudine", nullable = false, precision = 10, scale = 8)
    private BigDecimal latitudine;

    @NotNull
    @Digits(integer = 11, fraction = 8)
    @Column(name = "longitudine", nullable = false, precision = 11, scale = 8)
    private BigDecimal longitudine;

    // Getters and setters
    public String getPaese() { return paese; }
    public void setPaese(String paese) { this.paese = paese; }

    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }

    public String getCitta() { return citta; }
    public void setCitta(String citta) { this.citta = citta; }

    public String getVia() { return via; }
    public void setVia(String via) { this.via = via; }

    public String getCivico() { return civico; }
    public void setCivico(String civico) { this.civico = civico; }

    public String getEdificio() { return edificio; }
    public void setEdificio(String edificio) { this.edificio = edificio; }

    public BigDecimal getLatitudine() { return latitudine; }
    public void setLatitudine(BigDecimal latitudine) { this.latitudine = latitudine; }

    public BigDecimal getLongitudine() { return longitudine; }
    public void setLongitudine(BigDecimal longitudine) { this.longitudine = longitudine; }
}