package com.dieti.dietiestatesbackend.dto.response;

public class TerrenoResponse extends ImmobileResponse {
    private boolean haIngressoDallaStrada;

    // Getters and setters
    public boolean isHaIngressoDallaStrada() { return haIngressoDallaStrada; }
    public void setHaIngressoDallaStrada(boolean haIngressoDallaStrada) { this.haIngressoDallaStrada = haIngressoDallaStrada; }
}