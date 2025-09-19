package com.dieti.dietiestatesbackend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.dieti.dietiestatesbackend.validation.ValidPropertyCategory;
 
 /**
  * DTO per la creazione di un'autorimessa/garage.
  */
 @ValidPropertyCategory
 @Data
 @EqualsAndHashCode(callSuper = true)
 public final class CreateGaragePropertyRequest extends CreateBuildingPropertyRequest {
   
   private boolean hasSurveillance;
 
   @Min(1)
   private Integer numberOfFloors;

   @NotNull
   // Serve per indicare se il garage è interrato o meno, nello specifico a che piano si trova
   private Integer floor;
 
   @Override
   public com.dieti.dietiestatesbackend.enums.PropertyType getPropertyType() {
     return com.dieti.dietiestatesbackend.enums.PropertyType.GARAGE;
   }
 }