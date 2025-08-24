package com.dieti.dietiestatesbackend.dto.request;

import jakarta.validation.constraints.Min;

/**
 * DTO per la creazione di un'autorimessa/garage.
 */
public class CreateGaragePropertyRequest extends CreatePropertyRequest {

    private boolean haSorveglianza;

    @Min(1)
    private Integer numeroPiani;

    // Getters / Setters
    public boolean isHaSorveglianza() { return haSorveglianza; }
    public void setHaSorveglianza(boolean haSorveglianza) { this.haSorveglianza = haSorveglianza; }

    public Integer getNumeroPiani() { return numeroPiani; }
    public void setNumeroPiani(Integer numeroPiani) { this.numeroPiani = numeroPiani; }
}