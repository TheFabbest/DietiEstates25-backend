package com.dieti.dietiestatesbackend.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import com.dieti.dietiestatesbackend.enums.EnergyRating;
import com.dieti.dietiestatesbackend.enums.PropertyCondition;
import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Min;

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

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
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

    @Column(name = "image_directory_ulid", nullable = false, unique = true, length = 26)
    private String imageDirectoryUlid;

    @Min(1)
    @Column(name = "number_of_images", nullable = false)
    private int numberOfImages;
}
