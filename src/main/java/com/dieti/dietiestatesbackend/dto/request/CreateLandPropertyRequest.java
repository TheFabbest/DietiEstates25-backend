package com.dieti.dietiestatesbackend.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.dieti.dietiestatesbackend.validation.ValidPropertyCategory;

 /**
  * DTO per la creazione di un terreno.
  */
 @ValidPropertyCategory
 @Data
 @EqualsAndHashCode(callSuper = true)
 public final class CreateLandPropertyRequest extends AbstractCreatePropertyRequest implements CreatePropertyRequest {
     // Se il frontend fornisce informazioni specifiche per il terreno
     // (attualmente l'entità Land non ha campi specifici oltre ai default),
     // manteniamo un flag per indicare se è accessibile dalla strada.
     private Boolean hasRoadAccess;

     @Override
     public com.dieti.dietiestatesbackend.enums.PropertyType getPropertyType() {
         return com.dieti.dietiestatesbackend.enums.PropertyType.LAND;
     }
 }
