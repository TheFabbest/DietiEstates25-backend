package com.dieti.dietiestatesbackend.dto.request;

import java.math.BigDecimal;
import java.util.List;

import com.dieti.dietiestatesbackend.enums.EnergyRating;
import com.dieti.dietiestatesbackend.enums.Garden;
import com.dieti.dietiestatesbackend.enums.PropertyStatus;

import jakarta.validation.constraints.Pattern;

public class FilterRequest {

    // Common
    @Pattern(regexp = "COMMERCIAL|RESIDENTIAL|LAND|GARAGE", message = "Property category must be COMMERCIAL, RESIDENTIAL, LAND or GARAGE")
    private String category;

    @Pattern(regexp = "SALE|RENT", message = "Contract must be SALE or RENT")
    private String contract;

    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer minArea;
    private Integer minYearBuilt;
    private List<PropertyStatus> acceptedStatus;
    private EnergyRating minEnergyRating;

    // Commercial, Residential, Garage
    private Integer minNumberOfFloors;

    // Commercial, Residential
    private Integer minNumberOfRooms;
    private Integer minNumberOfBathrooms;

    // Residential
    private Integer minParkingSpaces;
    private String heating; // TODO see regex
    private List<Garden> acceptedGarden;
    private Boolean mustBeFurnished;
    private Boolean mustHaveElevator;

    // Commercial
    private Boolean mustHaveWheelchairAccess;
    private Integer minNumeroVetrine; // TODO see

    // Garage
    private Boolean mustHaveSurveillance;

    // Land
    private Boolean mustBeAccessibleFromStreet;

    // getters and setters
    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }

    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }

    public Integer getMinArea() { return minArea; }
    public void setMinArea(Integer area) { this.minArea = area; }

    public Integer getMinYearBuilt() { return minYearBuilt; }
    public void setMinYearBuilt(Integer yearBuilt) { this.minYearBuilt = yearBuilt; }

    public String getContract() { return contract; }
    public void setContract(String contract) { this.contract = contract; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<PropertyStatus> getAcceptedStatus() { return acceptedStatus; }
    public void setAcceptedStatus(List<PropertyStatus> acceptedStatus) { this.acceptedStatus = acceptedStatus; }

    public EnergyRating getMinEnergyRating() { return minEnergyRating; }
    public void setMinEnergyRating(EnergyRating minEnergyRating) { this.minEnergyRating = minEnergyRating; }

    public Integer getMinNumberOfFloors() { return minNumberOfFloors; }
    public void setMinNumberOfFloors(Integer minNumberOfFloors) { this.minNumberOfFloors = minNumberOfFloors; }

    public Integer getMinNumberOfRooms() { return minNumberOfRooms; }
    public void setMinNumberOfRooms(Integer minNumberOfRooms) { this.minNumberOfRooms = minNumberOfRooms; }

    public Integer getMinNumberOfBathrooms() { return minNumberOfBathrooms; }
    public void setMinNumberOfBathrooms(Integer minNumberOfBathrooms) { this.minNumberOfBathrooms = minNumberOfBathrooms; }

    public Integer getMinParkingSpaces() { return minParkingSpaces; }
    public void setMinParkingSpaces(Integer minParkingSpaces) { this.minParkingSpaces = minParkingSpaces; }

    public String getHeating() { return heating; }
    public void setHeating(String heating) { this.heating = heating; }

    public List<Garden> getAcceptedGarden() { return acceptedGarden; }
    public void setAcceptedGarden(List<Garden> acceptedGarden) { this.acceptedGarden = acceptedGarden; }

    public Boolean getMustBeFurnished() { return mustBeFurnished; }
    public void setMustBeFurnished(Boolean mustBeFurnished) { this.mustBeFurnished = mustBeFurnished; }

    public Boolean getMustHaveElevator() { return mustHaveElevator; }
    public void setMustHaveElevator(Boolean mustHaveElevator) { this.mustHaveElevator = mustHaveElevator; }

    public Boolean getMustHaveWheelchairAccess() { return mustHaveWheelchairAccess; }
    public void setMustHaveWheelchairAccess(Boolean mustHaveWheelchairAccess) { this.mustHaveWheelchairAccess = mustHaveWheelchairAccess; }

    public Integer getMinNumeroVetrine() { return minNumeroVetrine; }
    public void setMinNumeroVetrine(Integer minNumeroVetrine) { this.minNumeroVetrine = minNumeroVetrine; }

    public Boolean getMustHaveSurveillance() { return mustHaveSurveillance; }
    public void setMustHaveSurveillance(Boolean mustHaveSurveillance) { this.mustHaveSurveillance = mustHaveSurveillance; }

    public Boolean getMustBeAccessibleFromStreet() { return mustBeAccessibleFromStreet; }
    public void setMustBeAccessibleFromStreet(Boolean mustBeAccessibleFromStreet) { this.mustBeAccessibleFromStreet = mustBeAccessibleFromStreet; }
}
