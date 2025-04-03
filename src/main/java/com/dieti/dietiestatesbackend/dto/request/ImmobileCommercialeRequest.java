package com.dieti.dietiestatesbackend.dto.request;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ImmobileCommercialeRequest extends ImmobileRequest {
    @NotNull @Min(1)
    private Integer numeroLocali;
    
    @NotEmpty
    private List<String> piani;
    
    @NotNull @Min(1)
    private Integer numeroBagni;
    
    @NotNull @Min(1)
    private Integer numeroPianiTotali;
    
    private boolean haAccessoDisabili;
    
    @Min(0)
    private Integer numeroVetrine;

    // Getters and setters
    public Integer getNumeroLocali() { return numeroLocali; }
    public void setNumeroLocali(Integer numeroLocali) { this.numeroLocali = numeroLocali; }

    public List<String> getPiani() { return piani; }
    public void setPiani(List<String> piani) { this.piani = piani; }

    public Integer getNumeroBagni() { return numeroBagni; }
    public void setNumeroBagni(Integer numeroBagni) { this.numeroBagni = numeroBagni; }

    public Integer getNumeroPianiTotali() { return numeroPianiTotali; }
    public void setNumeroPianiTotali(Integer numeroPianiTotali) { this.numeroPianiTotali = numeroPianiTotali; }

    public boolean isHaAccessoDisabili() { return haAccessoDisabili; }
    public void setHaAccessoDisabili(boolean haAccessoDisabili) { this.haAccessoDisabili = haAccessoDisabili; }

    public Integer getNumeroVetrine() { return numeroVetrine; }
    public void setNumeroVetrine(Integer numeroVetrine) { this.numeroVetrine = numeroVetrine; }
}