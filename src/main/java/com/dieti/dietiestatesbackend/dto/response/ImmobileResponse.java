package com.dieti.dietiestatesbackend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ImmobileResponse {
    private Long id;
    private String descrizione;
    private BigDecimal prezzo;
    private Integer superficie;
    private Integer contratto;
    private Integer categoriaImmobile;
    private Integer statoImmobile;
    private Integer classeEnergetica;
    private Integer tipologiaProprieta;
    private List<String> caratteristicheAddizionali;
    private Integer agenteImmobiliare;
    private Integer indirizzo;
    private LocalDateTime ultimaModifica;
    private LocalDateTime createdAt;

    // Main class getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public BigDecimal getPrezzo() { return prezzo; }
    public void setPrezzo(BigDecimal prezzo) { this.prezzo = prezzo; }

    public Integer getSuperficie() { return superficie; }
    public void setSuperficie(Integer superficie) { this.superficie = superficie; }

    public Integer getContratto() { return contratto; }
    public void setContratto(Integer contratto) { this.contratto = contratto; }

    public Integer getCategoriaImmobile() { return categoriaImmobile; }
    public void setCategoriaImmobile(Integer categoriaImmobile) { this.categoriaImmobile = categoriaImmobile; }
    
    public Integer getStatoImmobile() { return statoImmobile; }
    public void setStatoImmobile(Integer statoImmobile) { this.statoImmobile = statoImmobile; }

    public Integer getClasseEnergetica() { return classeEnergetica; }
    public void setClasseEnergetica(Integer classeEnergetica) { this.classeEnergetica = classeEnergetica; }

    public Integer getTipologiaProprieta() { return tipologiaProprieta; }
    public void setTipologiaProprieta(Integer tipologiaProprieta) { this.tipologiaProprieta = tipologiaProprieta; }

    public List<String> getCaratteristicheAddizionali() { return caratteristicheAddizionali; }
    public void setCaratteristicheAddizionali(List<String> caratteristicheAddizionali) { this.caratteristicheAddizionali = caratteristicheAddizionali; }

    public Integer getAgenteImmobiliare() { return agenteImmobiliare; }
    public void setAgenteImmobiliare(Integer agenteImmobiliare) { this.agenteImmobiliare = agenteImmobiliare; }

    public Integer getIndirizzo() { return indirizzo; }
    public void setIndirizzo(Integer indirizzo) { this.indirizzo = indirizzo; }

    public LocalDateTime getUltimaModifica() { return ultimaModifica; }
    public void setUltimaModifica(LocalDateTime ultimaModifica) { this.ultimaModifica = ultimaModifica; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}