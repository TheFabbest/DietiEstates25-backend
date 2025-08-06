package com.dieti.dietiestatesbackend.dto.request;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

public class AutorimessaRequest extends PropertyRequest {
    private boolean haSorveglianza = false;
    
    @NotEmpty
    private List<String> piani;
    
    @Min(1)
    private Integer numeroPiani = 1;

    // Getters and setters
    public boolean haSorveglianza() { return haSorveglianza; }
    public void setHaSorveglianza(boolean haSorveglianza) { this.haSorveglianza = haSorveglianza; }

    public List<String> getPiani() { return piani; }
    public void setPiani(List<String> piani) { this.piani = piani; }

    public Integer getNumeroPiani() { return numeroPiani; }
    public void setNumeroPiani(Integer numeroPiani) { this.numeroPiani = numeroPiani; }
}