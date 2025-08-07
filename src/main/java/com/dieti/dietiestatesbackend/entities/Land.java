package com.dieti.dietiestatesbackend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "land")
public class Land extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id", foreignKey = @ForeignKey(name = "fk_land_property"))
    private Property property;

    @Column(name = "accessible_from_street")
    private boolean accessibleFromStreet = true;

    // Getters and setters
    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }

    public boolean getAccessibleFromStreet() { return accessibleFromStreet; }
    public void setAccessibleFromStreet(boolean accessible) { this.accessibleFromStreet = accessible; }
}