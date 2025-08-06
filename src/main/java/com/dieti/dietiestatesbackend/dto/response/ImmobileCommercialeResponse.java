package com.dieti.dietiestatesbackend.dto.response;

import java.util.List;

public class ImmobileCommercialeResponse extends PropertyResponse {
    private Integer numeroLocali;
    private List<String> piani;
    private Integer numeroBagni;
    private Integer numeroPianiTotali;
    private boolean haAccessoDisabili;
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