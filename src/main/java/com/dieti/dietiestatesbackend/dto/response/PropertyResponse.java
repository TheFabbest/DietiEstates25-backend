package com.dieti.dietiestatesbackend.dto.response;

import java.math.BigDecimal;

public class PropertyResponse {
    private Long id;
    private String description;
    private BigDecimal price;
    private Integer area;
    private String contract;
    private String propertyCategory;
    private String status;
    private String energyClass;
    // TODO see private List<String> caratteristicheAddizionali;
    private Long id_agent;
    private String address;
    // TODO see private LocalDateTime createdAt;
    // TODO add images

    // Main class getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getArea() { return area; }
    public void setArea(Integer area) { this.area = area; }

    public String getContract() { return contract; }
    public void setContract(String contract) { this.contract = contract; }

    public String getPropertyCategory() { return propertyCategory; }
    public void setPropertyCategory(String category) { this.propertyCategory = category; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getEnergyClass() { return energyClass; }
    public void setEnergyClass(String energyClass) { this.energyClass = energyClass; }

    public Long getId_agent() { return id_agent; }
    public void setId_agent(Long agent) { this.id_agent = agent; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}