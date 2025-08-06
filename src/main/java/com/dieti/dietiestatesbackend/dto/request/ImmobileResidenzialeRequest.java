package com.dieti.dietiestatesbackend.dto.request;

import java.util.List;

import com.dieti.dietiestatesbackend.enums.Giardino;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ImmobileResidenzialeRequest extends PropertyRequest {
    @NotNull @Min(1)
    private Integer numeroLocali;
    
    @NotNull @Min(1)
    private Integer numeroBagni;
    
    @Min(0)
    private Integer postiAuto;
    
    @NotNull
    private Long idRiscaldamento;
    
    @NotNull
    private Giardino giardino;
    
    private boolean isArredato;
    
    @NotEmpty
    private List<String> piani;
    
    @NotNull @Min(1)
    private Integer numeroPianiTotali;
    
    private boolean haAscensore;

    // Getters and setters
    public Integer getNumeroLocali() { return numeroLocali; }
    public void setNumeroLocali(Integer numeroLocali) { this.numeroLocali = numeroLocali; }

    public Integer getNumeroBagni() { return numeroBagni; }
    public void setNumeroBagni(Integer numeroBagni) { this.numeroBagni = numeroBagni; }

    public Integer getPostiAuto() { return postiAuto; }
    public void setPostiAuto(Integer postiAuto) { this.postiAuto = postiAuto; }

    public Long getIdRiscaldamento() { return idRiscaldamento; }
    public void setIdRiscaldamento(Long idRiscaldamento) { this.idRiscaldamento = idRiscaldamento; }

    public Giardino getGiardino() { return giardino; }
    public void setGiardino(Giardino giardino) { this.giardino = giardino; }

    public boolean isArredato() { return isArredato; }
    public void setArredato(boolean arredato) { isArredato = arredato; }

    public List<String> getPiani() { return piani; }
    public void setPiani(List<String> piani) { this.piani = piani; }

    public Integer getNumeroPianiTotali() { return numeroPianiTotali; }
    public void setNumeroPianiTotali(Integer numeroPianiTotali) { this.numeroPianiTotali = numeroPianiTotali; }

    public boolean isHaAscensore() { return haAscensore; }
    public void setHaAscensore(boolean haAscensore) { this.haAscensore = haAscensore; }
}