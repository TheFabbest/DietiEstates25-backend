package com.dieti.dietiestatesbackend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.dieti.dietiestatesbackend.enums.ClasseEnergetica;
import com.dieti.dietiestatesbackend.enums.StatoImmobile;
import com.dieti.dietiestatesbackend.enums.TipologiaProprieta;

public class ImmobileResponse {
    private Long id;
    private String description;
    private BigDecimal prezzo;
    private Integer superficie;
    private ContrattoDTO contratto;
    private CategoriaImmobileDTO categoriaImmobile;
    private StatoImmobile statoImmobile;
    private ClasseEnergetica classeEnergetica;
    private TipologiaProprieta tipologiaProprieta;
    private List<String> caratteristicheAddizionali;
    private UtenteDTO agenteImmobiliare;
    private IndirizzoDTO indirizzo;
    private List<String> immagini;
    private LocalDateTime ultimaModifica;
    private LocalDateTime createdAt;

    // Basic nested DTOs
    public static class ContrattoDTO {
        private Long id;
        private String nome;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
    }

    public static class CategoriaImmobileDTO {
        private Long id;
        private String categoria;
        private String sottocategoria;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getCategoria() { return categoria; }
        public void setCategoria(String categoria) { this.categoria = categoria; }

        public String getSottocategoria() { return sottocategoria; }
        public void setSottocategoria(String sottocategoria) { this.sottocategoria = sottocategoria; }
    }

    public static class UtenteDTO {
        private Long id;
        private String username;
        private String nome;
        private String cognome;
        private String email;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getCognome() { return cognome; }
        public void setCognome(String cognome) { this.cognome = cognome; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class IndirizzoDTO {
        private Long id;
        private String via;
        private String civico;
        private String interno;
        private String cap;
        private String comune;
        private String provincia;
        private String codiceNazionale;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getVia() { return via; }
        public void setVia(String via) { this.via = via; }

        public String getCivico() { return civico; }
        public void setCivico(String civico) { this.civico = civico; }

        public String getInterno() { return interno; }
        public void setInterno(String interno) { this.interno = interno; }

        public String getCap() { return cap; }
        public void setCap(String cap) { this.cap = cap; }

        public String getComune() { return comune; }
        public void setComune(String comune) { this.comune = comune; }

        public String getProvincia() { return provincia; }
        public void setProvincia(String provincia) { this.provincia = provincia; }

        public String getCodiceNazionale() { return codiceNazionale; }
        public void setCodiceNazionale(String codiceNazionale) { this.codiceNazionale = codiceNazionale; }
    }

    // Main class getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrezzo() { return prezzo; }
    public void setPrezzo(BigDecimal prezzo) { this.prezzo = prezzo; }

    public Integer getSuperficie() { return superficie; }
    public void setSuperficie(Integer superficie) { this.superficie = superficie; }

    public ContrattoDTO getContratto() { return contratto; }
    public void setContratto(ContrattoDTO contratto) { this.contratto = contratto; }

    public CategoriaImmobileDTO getCategoriaImmobile() { return categoriaImmobile; }
    public void setCategoriaImmobile(CategoriaImmobileDTO categoriaImmobile) { this.categoriaImmobile = categoriaImmobile; }

    public StatoImmobile getStatoImmobile() { return statoImmobile; }
    public void setStatoImmobile(StatoImmobile statoImmobile) { this.statoImmobile = statoImmobile; }

    public ClasseEnergetica getClasseEnergetica() { return classeEnergetica; }
    public void setClasseEnergetica(ClasseEnergetica classeEnergetica) { this.classeEnergetica = classeEnergetica; }

    public TipologiaProprieta getTipologiaProprieta() { return tipologiaProprieta; }
    public void setTipologiaProprieta(TipologiaProprieta tipologiaProprieta) { this.tipologiaProprieta = tipologiaProprieta; }

    public List<String> getCaratteristicheAddizionali() { return caratteristicheAddizionali; }
    public void setCaratteristicheAddizionali(List<String> caratteristicheAddizionali) { this.caratteristicheAddizionali = caratteristicheAddizionali; }

    public UtenteDTO getAgenteImmobiliare() { return agenteImmobiliare; }
    public void setAgenteImmobiliare(UtenteDTO agenteImmobiliare) { this.agenteImmobiliare = agenteImmobiliare; }

    public IndirizzoDTO getIndirizzo() { return indirizzo; }
    public void setIndirizzo(IndirizzoDTO indirizzo) { this.indirizzo = indirizzo; }

    public List<String> getImmagini() { return immagini; }
    public void setImmagini(List<String> immagini) { this.immagini = immagini; }

    public LocalDateTime getUltimaModifica() { return ultimaModifica; }
    public void setUltimaModifica(LocalDateTime ultimaModifica) { this.ultimaModifica = ultimaModifica; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}