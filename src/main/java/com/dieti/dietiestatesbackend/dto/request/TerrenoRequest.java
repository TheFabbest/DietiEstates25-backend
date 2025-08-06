package com.dieti.dietiestatesbackend.dto.request;

public class TerrenoRequest extends PropertyRequest {
    private boolean haIngressoDallaStrada = true;

    // Getters and setters
    public boolean isHaIngressoDallaStrada() { return haIngressoDallaStrada; }
    public void setHaIngressoDallaStrada(boolean haIngressoDallaStrada) { this.haIngressoDallaStrada = haIngressoDallaStrada; }
}