package com.dieti.dietiestatesbackend.dto.response;

import java.math.BigDecimal;

public class PropertyResponse {
    private Long id;
    private String description;
    private BigDecimal price;
    private Integer area;
    private Long id_contract;
    private Long id_propertyCategory;
    private Long id_status;
    private Long id_energyClass;
    // TODO see private List<String> caratteristicheAddizionali;
    private Long id_agent;
    private Long id_address;
    // TODO see private LocalDateTime createdAt;

    // Main class getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getArea() { return area; }
    public void setArea(Integer area) { this.area = area; }

    public Long getId_contract() { return id_contract; }
    public void setId_contract(Long contract) { this.id_contract = contract; }

    public Long getId_propertyCategory() { return id_propertyCategory; }
    public void setId_propertyCategory(Long category) { this.id_propertyCategory = category; }
    
    public Long getId_status() { return id_status; }
    public void setId_status(Long status) { this.id_status = status; }

    public Long getId_energyClass() { return id_energyClass; }
    public void setId_energyClass(Long energyClass) { this.id_energyClass = energyClass; }

    public Long getId_agent() { return id_agent; }
    public void setId_agent(Long agent) { this.id_agent = agent; }

    public Long getId_address() { return id_address; }
    public void setId_address(Long address) { this.id_address = address; }
}