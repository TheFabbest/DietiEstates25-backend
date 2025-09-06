package com.dieti.dietiestatesbackend.dto.response;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
 
 /**
  * English response DTO translated from AutorimessaResponse.
  */
 @Data
 @EqualsAndHashCode(callSuper = true)
 public class GarageResponse extends PropertyResponse {
     private boolean hasSurveillance;
     private List<String> floors;
     private Integer numberOfFloors;
 }