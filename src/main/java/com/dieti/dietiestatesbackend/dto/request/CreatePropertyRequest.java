package com.dieti.dietiestatesbackend.dto.request;

import java.math.BigDecimal;
import java.util.List;

import com.dieti.dietiestatesbackend.enums.EnergyRating;
import com.dieti.dietiestatesbackend.enums.PropertyStatus;
import com.dieti.dietiestatesbackend.enums.PropertyType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * DTO base unificato per la creazione di proprietà.
 * Ora supporta polimorfismo JSON per deserializzare automaticamente il DTO specifico
 * basato sul campo `propertyType`.
 *
 * Nota: rimosse le additionalProperties non tipizzate per favorire DTO fortemente tipizzati.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "propertyType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateResidentialPropertyRequest.class, name = "RESIDENTIAL"),
        @JsonSubTypes.Type(value = CreateCommercialPropertyRequest.class, name = "COMMERCIAL"),
        @JsonSubTypes.Type(value = CreateLandPropertyRequest.class, name = "LAND"),
        @JsonSubTypes.Type(value = CreateGaragePropertyRequest.class, name = "GARAGE")
})
public class CreatePropertyRequest {
    // Tipo di proprietà (RESIDENTIAL, COMMERCIAL, LAND, GARAGE)
    private PropertyType propertyType;

    private String description;
    private BigDecimal price;
    private Integer area;
    private Integer yearBuilt;

    // Contratto: opzionale (il client invia il tipo/nome, non l'id)
    private String contractType;

    // Riferimenti a lookup: il client invia il nome della categoria, non l'id
    private String propertyCategoryName;
    private PropertyStatus status;
    private EnergyRating energyRating;

    // Agente e indirizzo (o indirizzo inline): il client fornisce agentUsername
    private String agentUsername;
    private Long addressId;
    private AddressRequest addressRequest;

    // Immagini (paths)
    private List<String> images;

    // Getters / Setters
    public PropertyType getPropertyType() { return propertyType; }
    public void setPropertyType(PropertyType propertyType) { this.propertyType = propertyType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getArea() { return area; }
    public void setArea(Integer area) { this.area = area; }

    public Integer getYearBuilt() { return yearBuilt; }
    public void setYearBuilt(Integer yearBuilt) { this.yearBuilt = yearBuilt; }

    public String getContractType() { return contractType; }
    public void setContractType(String contractType) { this.contractType = contractType; }

    public String getPropertyCategoryName() { return propertyCategoryName; }
    public void setPropertyCategoryName(String propertyCategoryName) { this.propertyCategoryName = propertyCategoryName; }

    public PropertyStatus getStatus() { return status; }
    public void setStatus(PropertyStatus status) { this.status = status; }

    public EnergyRating getEnergyRating() { return energyRating; }
    public void setEnergyRating(EnergyRating energyRating) { this.energyRating = energyRating; }

    public String getAgentUsername() { return agentUsername; }
    public void setAgentUsername(String agentUsername) { this.agentUsername = agentUsername; }

    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }

    public AddressRequest getAddressRequest() { return addressRequest; }
    public void setAddressRequest(AddressRequest addressRequest) { this.addressRequest = addressRequest; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
}