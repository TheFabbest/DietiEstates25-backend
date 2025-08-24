package com.dieti.dietiestatesbackend.entities;
 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
 
@Entity
@Table(name = "land")
@PrimaryKeyJoinColumn(name = "id")
public class Land extends Property {
 
 
    @Column(name = "accessible_from_street")
    private boolean accessibleFromStreet = true;
 
    // Getters and setters
 
    public boolean getAccessibleFromStreet() { return accessibleFromStreet; }
    public void setAccessibleFromStreet(boolean accessible) { this.accessibleFromStreet = accessible; }
 
    @Override
    public void accept(PropertyVisitor visitor) {
        visitor.visit(this);
    }
}