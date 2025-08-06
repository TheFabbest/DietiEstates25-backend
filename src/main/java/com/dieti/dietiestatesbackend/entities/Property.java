package com.dieti.dietiestatesbackend.entities;

import java.math.BigDecimal;
import com.dieti.dietiestatesbackend.enums.EnergyRating;
import com.dieti.dietiestatesbackend.enums.PropertyStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "Property")
public class Property extends BaseEntity {

    @Column(name = "description")
    private String description;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @NotNull
    @Min(1)
    @Column(name = "area", nullable = false)
    private Integer area;
    
    @Min(1)
    @Column(name = "year_built", nullable = true)
    private Integer yearBuilt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contract", nullable = false, foreignKey = @ForeignKey(name = "fk_property_contract"))
    private Contract contract;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_property_category", nullable = false, foreignKey = @ForeignKey(name = "fk_property_propertycategory"))
    private PropertyCategory propertyCategory;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PropertyStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "energy_rating", nullable = false)
    private EnergyRating energyRating;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agent", nullable = false, foreignKey = @ForeignKey(name = "fk_property_agent"))
    private User agent;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_address", nullable = false, foreignKey = @ForeignKey(name = "fk_property_address"))
    private Address address;

    // TODO createdAt ?
    // TODO additional features

    // Getters and setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getArea() { return area; }
    public void setArea(Integer area) { this.area = area; }

    public Contract getContract() { return contract; }
    public void setContract(Contract contract) { this.contract = contract; }

    public PropertyCategory getPropertyCategory() { return propertyCategory; }
    public void setPropertyCategory(PropertyCategory category) { this.propertyCategory = category; }

    public PropertyStatus getStatus() { return status; }
    public void setStatus(PropertyStatus status) { this.status = status; }

    public EnergyRating getEnergyRating() { return energyRating; }
    public void setEnergyRating(EnergyRating rating) { this.energyRating = rating; }

    public User getAgent() { return agent; }
    public void setAgent(User agent) { this.agent = agent; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
}