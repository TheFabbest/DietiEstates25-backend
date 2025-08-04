package com.dieti.dietiestatesbackend.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.dieti.dietiestatesbackend.enums.ClasseEnergetica;
import com.dieti.dietiestatesbackend.enums.StatoImmobile;
import com.dieti.dietiestatesbackend.enums.TipologiaProprieta;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "immobile")
@SequenceGenerator(
    name = "immobile_seq",
    sequenceName = "dieti_estates.immobile_id_seq",
    allocationSize = 1
)
public class Immobile extends BaseEntity {

    @Column(name = "description")
    private String description;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(name = "prezzo", nullable = false, precision = 12, scale = 2)
    private BigDecimal prezzo;

    @NotNull
    @Min(1)
    @Column(name = "superficie", nullable = false)
    private Integer superficie;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contratto", nullable = false, foreignKey = @ForeignKey(name = "fk_immobile_contratto"))
    private Contratto contratto;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria_immobile", nullable = false, foreignKey = @ForeignKey(name = "fk_immobile_categoria"))
    private CategoriaImmobile categoriaImmobile;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "stato_immobile", nullable = false)
    private StatoImmobile statoImmobile;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "classe_energetica", nullable = false)
    private ClasseEnergetica classeEnergetica;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipologia_proprieta")
    private TipologiaProprieta tipologiaProprieta;

    @ElementCollection
    @CollectionTable(name = "immobile_caratteristiche", joinColumns = @JoinColumn(name = "immobile_id"))
    @Column(name = "caratteristica")
    private List<String> caratteristicheAddizionali;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agente_immobiliare", nullable = false, foreignKey = @ForeignKey(name = "fk_immobile_agente"))
    private Utente agenteImmobiliare;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_indirizzo", nullable = false, foreignKey = @ForeignKey(name = "fk_immobile_indirizzo"))
    private Indirizzo indirizzo;

    @NotEmpty
    @ElementCollection
    @CollectionTable(name = "immobile_immagini", joinColumns = @JoinColumn(name = "immobile_id"))
    @Column(name = "immagine_url", nullable = false)
    private List<String> immagini;

    @Column(name = "ultima_modifica", nullable = false)
    private LocalDateTime ultimaModifica = LocalDateTime.now();

    // Getters and setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrezzo() { return prezzo; }
    public void setPrezzo(BigDecimal prezzo) { this.prezzo = prezzo; }

    public Integer getSuperficie() { return superficie; }
    public void setSuperficie(Integer superficie) { this.superficie = superficie; }

    public Contratto getContratto() { return contratto; }
    public void setContratto(Contratto contratto) { this.contratto = contratto; }

    public CategoriaImmobile getCategoriaImmobile() { return categoriaImmobile; }
    public void setCategoriaImmobile(CategoriaImmobile categoriaImmobile) { this.categoriaImmobile = categoriaImmobile; }

    public StatoImmobile getStatoImmobile() { return statoImmobile; }
    public void setStatoImmobile(StatoImmobile statoImmobile) { this.statoImmobile = statoImmobile; }

    public ClasseEnergetica getClasseEnergetica() { return classeEnergetica; }
    public void setClasseEnergetica(ClasseEnergetica classeEnergetica) { this.classeEnergetica = classeEnergetica; }

    public TipologiaProprieta getTipologiaProprieta() { return tipologiaProprieta; }
    public void setTipologiaProprieta(TipologiaProprieta tipologiaProprieta) { this.tipologiaProprieta = tipologiaProprieta; }

    public List<String> getCaratteristicheAddizionali() { return caratteristicheAddizionali; }
    public void setCaratteristicheAddizionali(List<String> caratteristicheAddizionali) { this.caratteristicheAddizionali = caratteristicheAddizionali; }

    public Utente getAgenteImmobiliare() { return agenteImmobiliare; }
    public void setAgenteImmobiliare(Utente agenteImmobiliare) { this.agenteImmobiliare = agenteImmobiliare; }

    public Indirizzo getIndirizzo() { return indirizzo; }
    public void setIndirizzo(Indirizzo indirizzo) { this.indirizzo = indirizzo; }

    public List<String> getImmagini() { return immagini; }
    public void setImmagini(List<String> immagini) { this.immagini = immagini; }

    public LocalDateTime getUltimaModifica() { return ultimaModifica; }
    public void setUltimaModifica(LocalDateTime ultimaModifica) { this.ultimaModifica = ultimaModifica; }
}