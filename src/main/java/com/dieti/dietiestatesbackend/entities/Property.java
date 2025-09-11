package com.dieti.dietiestatesbackend.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.dieti.dietiestatesbackend.enums.EnergyRating;
import com.dieti.dietiestatesbackend.enums.PropertyCondition;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Table(name = "property")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "property_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Property extends BaseEntity {

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
    @Column(name = "condition", nullable = false)
    private PropertyCondition condition;

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

    @Column(name = "additional_features")
    private String additionalFeatures;

    @ElementCollection
    @CollectionTable(name = "property_images", joinColumns = @JoinColumn(name = "id_property", foreignKey = @ForeignKey(name = "fk_property_images_property")))
    @Column(name = "image_path")
    private List<String> images = new ArrayList<>();

}
