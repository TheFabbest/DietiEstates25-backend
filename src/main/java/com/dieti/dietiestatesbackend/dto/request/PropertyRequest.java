package com.dieti.dietiestatesbackend.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PropertyRequest {
    @NotBlank
    private String description;
    
    @NotNull @DecimalMin("0.01")
    private BigDecimal price;
    
    @NotNull @Min(1)
    private Integer area;
    
    @NotNull
    private Long id_contract;
    
    @NotNull
    private Long id_propertyCategory;
    
    @NotNull
    private Long id_condition;
    
    @NotNull
    private Long id_energyClass;
    
    // TODO see private List<String> caratteristicheAddizionali;
    
    @NotNull
    private Long id_agent;
    
    @NotNull
    private Long id_address;

    // Getters and setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal prezzo) { this.price = prezzo; }

    public Integer getArea() { return area; }
    public void setArea(Integer superficie) { this.area = superficie; }

    public Long getIdContract() { return id_contract; }
    public void setIdContract(Long id_contract) { this.id_contract = id_contract; }

    public Long getIdPropertyCategory() { return id_propertyCategory; }
    public void setIdPropertyCategory(Long id_propertyCategory) { this.id_propertyCategory = id_propertyCategory; }

    public Long getId_condition() { return id_condition; }
    public void setId_condition(Long id_condition) { this.id_condition = id_condition; }

    public Long getId_energyClass() { return id_energyClass; }
    public void setId_energyClass(Long id_energyClass) { this.id_energyClass = id_energyClass; }

    public Long getId_agent() { return id_agent; }
    public void setId_agent(Long idAgenteImmobiliare) { this.id_agent = idAgenteImmobiliare; }

    public Long getId_address() { return id_address; }
    public void setId_address(Long address) { this.id_address = address; }
}
