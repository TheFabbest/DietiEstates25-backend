package com.dieti.dietiestatesbackend.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class IndirizzoRequest {

    @NotBlank
    private String paese;

    @NotBlank
    private String provincia;

    @NotBlank
    private String citta;

    @NotBlank
    private String via;

    private String civico;

    private String edificio;

    @NotNull
    @Digits(integer = 10, fraction = 8)
    private BigDecimal latitudine;

    @NotNull
    @Digits(integer = 11, fraction = 8)
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