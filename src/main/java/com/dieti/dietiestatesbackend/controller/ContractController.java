package com.dieti.dietiestatesbackend.controller;
 
 import com.dieti.dietiestatesbackend.entities.Contract;
 import com.dieti.dietiestatesbackend.repositories.ContractRepository;
 import org.springframework.security.access.prepost.PreAuthorize;
 import org.springframework.web.bind.annotation.GetMapping;
 import org.springframework.web.bind.annotation.RestController;
 
 import java.util.List;
 
 @RestController
 public class ContractController {
     private final ContractRepository contractRepository;

     public ContractController(ContractRepository contractRepository) {
         this.contractRepository = contractRepository;
     }
 
     @PreAuthorize("authentication.principal.isManager")
     @GetMapping("/contracts")
     public List<Contract> getAllContracts() {
         return contractRepository.findAll();
     }
 }