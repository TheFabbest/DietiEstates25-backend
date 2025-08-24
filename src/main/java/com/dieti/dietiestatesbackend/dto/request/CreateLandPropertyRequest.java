package com.dieti.dietiestatesbackend.dto.request;

/**
 * DTO per la creazione di un terreno.
 */
public class CreateLandPropertyRequest extends CreatePropertyRequest {
    // Se il frontend fornisce informazioni specifiche per il terreno
    // (attualmente l'entità Land non ha campi specifici oltre ai default),
    // manteniamo un flag per indicare se è accessibile dalla strada.
    private Boolean haIngressoDallaStrada;

    public Boolean getHaIngressoDallaStrada() { return haIngressoDallaStrada; }
    public void setHaIngressoDallaStrada(Boolean haIngressoDallaStrada) { this.haIngressoDallaStrada = haIngressoDallaStrada; }
}