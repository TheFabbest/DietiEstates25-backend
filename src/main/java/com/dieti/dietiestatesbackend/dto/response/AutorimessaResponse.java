package com.dieti.dietiestatesbackend.dto.response;

import java.util.List;

public class AutorimessaResponse extends ImmobileResponse {
    private boolean haSorveglianza;
    private List<String> piani;
    private Integer numeroPiani;

    // Getters and setters
    public boolean isHaSorveglianza() { return haSorveglianza; }
    public void setHaSorveglianza(boolean haSorveglianza) { this.haSorveglianza = haSorveglianza; }

    public List<String> getPiani() { return piani; }
    public void setPiani(List<String> piani) { this.piani = piani; }

    public Integer getNumeroPiani() { return numeroPiani; }
    public void setNumeroPiani(Integer numeroPiani) { this.numeroPiani = numeroPiani; }
}