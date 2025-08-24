package com.dieti.dietiestatesbackend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO per la creazione di un immobile commerciale.
 */
public class CreateCommercialPropertyRequest extends CreatePropertyRequest {

    @NotNull @Min(1)
    private Integer numeroLocali;

    @NotNull
    private Integer piano;

    @NotNull @Min(1)
    private Integer numeroBagni;

    @NotNull @Min(1)
    private Integer numeroPianiTotali;

    private boolean haAccessoDisabili;

    @Min(0)
    private Integer numeroVetrine;

    // Getters / Setters
    public Integer getNumeroLocali() { return numeroLocali; }
    public void setNumeroLocali(Integer numeroLocali) { this.numeroLocali = numeroLocali; }

    public Integer getPiano() { return piano; }
    public void setPiano(Integer piano) { this.piano = piano; }

    public Integer getNumeroBagni() { return numeroBagni; }
    public void setNumeroBagni(Integer numeroBagni) { this.numeroBagni = numeroBagni; }

    public Integer getNumeroPianiTotali() { return numeroPianiTotali; }
    public void setNumeroPianiTotali(Integer numeroPianiTotali) { this.numeroPianiTotali = numeroPianiTotali; }

    public boolean isHaAccessoDisabili() { return haAccessoDisabili; }
    public void setHaAccessoDisabili(boolean haAccessoDisabili) { this.haAccessoDisabili = haAccessoDisabili; }

    public Integer getNumeroVetrine() { return numeroVetrine; }
    public void setNumeroVetrine(Integer numeroVetrine) { this.numeroVetrine = numeroVetrine; }
}