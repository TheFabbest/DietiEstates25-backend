package com.dieti.dietiestatesbackend.dto.request;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PropertyRequest {

    @NotBlank
    private String description;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price;

    @NotNull
    @Min(1)
    private Integer area;

    @JsonProperty("id_contract")
    @NotNull
    private Long contractId;

    @JsonProperty("id_propertyCategory")
    @NotNull
    private Long propertyCategoryId;

    @JsonProperty("id_condition")
    @NotNull
    private Long conditionId;

    @JsonProperty("id_energyClass")
    @NotNull
    private Long energyClassId;

    @JsonProperty("id_agent")
    @NotNull
    private Long agentId;

    @JsonProperty("id_address")
    @NotNull
    private Long addressId;

    // Getters and setters

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getArea() { return area; }
    public void setArea(Integer area) { this.area = area; }

    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }

    public Long getPropertyCategoryId() { return propertyCategoryId; }
    public void setPropertyCategoryId(Long propertyCategoryId) { this.propertyCategoryId = propertyCategoryId; }

    public Long getConditionId() { return conditionId; }
    public void setConditionId(Long conditionId) { this.conditionId = conditionId; }

    public Long getEnergyClassId() { return energyClassId; }
    public void setEnergyClassId(Long energyClassId) { this.energyClassId = energyClassId; }

    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }

    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }
}
