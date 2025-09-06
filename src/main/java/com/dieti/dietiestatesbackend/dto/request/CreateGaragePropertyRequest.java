package com.dieti.dietiestatesbackend.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
 
 /**
  * DTO per la creazione di un'autorimessa/garage.
  */
@Data
@EqualsAndHashCode(callSuper = true)
public final class CreateGaragePropertyRequest extends CreateBuildingPropertyRequest {
  
  private boolean hasSurveillance;

  @Min(1)
  private Integer numberOfFloors;

}