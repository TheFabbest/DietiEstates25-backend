package com.dieti.dietiestatesbackend.dto.request;

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
 
   @Override
   public com.dieti.dietiestatesbackend.enums.PropertyType getPropertyType() {
     return com.dieti.dietiestatesbackend.enums.PropertyType.GARAGE;
   }
 }