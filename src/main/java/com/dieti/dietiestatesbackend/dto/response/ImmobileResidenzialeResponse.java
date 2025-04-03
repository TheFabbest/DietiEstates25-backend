package com.dieti.dietiestatesbackend.dto.response;

import java.util.List;

import com.dieti.dietiestatesbackend.enums.Giardino;

public class ImmobileResidenzialeResponse extends ImmobileResponse {
    private Integer numeroLocali;
    private Integer numeroBagni;
    private Integer postiAuto;
    private RiscaldamentoDTO riscaldamento;
    private Giardino giardino;
    private boolean isArredato;
    private List<String> piani;
    private Integer numeroPianiTotali;
    private boolean haAscensore;

    // Nested DTO for Riscaldamento
    public static class RiscaldamentoDTO {
        private Long id;
        private String tipo;
        private boolean isAttivo;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }

        public boolean isAttivo() { return isAttivo; }
        public void setAttivo(boolean attivo) { isAttivo = attivo; }
    }

    // Getters and setters
    public Integer getNumeroLocali() { return numeroLocali; }
    public void setNumeroLocali(Integer numeroLocali) { this.numeroLocali = numeroLocali; }

    public Integer getNumeroBagni() { return numeroBagni; }
    public void setNumeroBagni(Integer numeroBagni) { this.numeroBagni = numeroBagni; }

    public Integer getPostiAuto() { return postiAuto; }
    public void setPostiAuto(Integer postiAuto) { this.postiAuto = postiAuto; }

    public RiscaldamentoDTO getRiscaldamento() { return riscaldamento; }
    public void setRiscaldamento(RiscaldamentoDTO riscaldamento) { this.riscaldamento = riscaldamento; }

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