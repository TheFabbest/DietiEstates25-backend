package com.dieti.dietiestatesbackend.entities;
 
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
 
@Entity
@Table(name = "land")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("LAND")
@Getter
@Setter
public class Land extends Property {
 
    @Column(name = "accessible_from_street")
    private boolean accessibleFromStreet = true;
 
}