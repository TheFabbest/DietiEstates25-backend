package com.dieti.dietiestatesbackend.dto.request;

import java.math.BigDecimal;
import java.util.List;

import com.dieti.dietiestatesbackend.enums.ClasseEnergetica;
import com.dieti.dietiestatesbackend.enums.StatoImmobile;
import com.dieti.dietiestatesbackend.enums.TipologiaProprieta;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ImmobileRequest {
    @NotBlank
    private String description;
    
    @NotNull @DecimalMin("0.01")
    private BigDecimal prezzo;
    
    @NotNull @Min(1)
    private Integer superficie;
    
    @NotNull
    private Long idContratto;
    
    @NotNull
    private Long idCategoriaImmobile;
    
    @NotNull
    private StatoImmobile statoImmobile;
    
    @NotNull
    private ClasseEnergetica classeEnergetica;
    
    private TipologiaProprieta tipologiaProprieta;
    
    private List<String> caratteristicheAddizionali;
    
    @NotNull
    private Long idAgenteImmobiliare;
    
    @NotNull
    private IndirizzoRequest indirizzo;
    
    @NotEmpty
    private List<String> immagini;

    // Getters and setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrezzo() { return prezzo; }
    public void setPrezzo(BigDecimal prezzo) { this.prezzo = prezzo; }

    public Integer getSuperficie() { return superficie; }
    public void setSuperficie(Integer superficie) { this.superficie = superficie; }

    public Long getIdContratto() { return idContratto; }
    public void setIdContratto(Long idContratto) { this.idContratto = idContratto; }

    public Long getIdCategoriaImmobile() { return idCategoriaImmobile; }
    public void setIdCategoriaImmobile(Long idCategoriaImmobile) { this.idCategoriaImmobile = idCategoriaImmobile; }

    public StatoImmobile getStatoImmobile() { return statoImmobile; }
    public void setStatoImmobile(StatoImmobile statoImmobile) { this.statoImmobile = statoImmobile; }

    public ClasseEnergetica getClasseEnergetica() { return classeEnergetica; }
    public void setClasseEnergetica(ClasseEnergetica classeEnergetica) { this.classeEnergetica = classeEnergetica; }

    public TipologiaProprieta getTipologiaProprieta() { return tipologiaProprieta; }
    public void setTipologiaProprieta(TipologiaProprieta tipologiaProprieta) { this.tipologiaProprieta = tipologiaProprieta; }

    public List<String> getCaratteristicheAddizionali() { return caratteristicheAddizionali; }
    public void setCaratteristicheAddizionali(List<String> caratteristicheAddizionali) { this.caratteristicheAddizionali = caratteristicheAddizionali; }

    public Long getIdAgenteImmobiliare() { return idAgenteImmobiliare; }
    public void setIdAgenteImmobiliare(Long idAgenteImmobiliare) { this.idAgenteImmobiliare = idAgenteImmobiliare; }

    public IndirizzoRequest getIndirizzo() { return indirizzo; }
    public void setIndirizzo(IndirizzoRequest indirizzo) { this.indirizzo = indirizzo; }

    public List<String> getImmagini() { return immagini; }
    public void setImmagini(List<String> immagini) { this.immagini = immagini; }
}
