package com.dieti.dietiestatesbackend.repositories;
 
import java.util.Optional;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import com.dieti.dietiestatesbackend.entities.Contract;
 
@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findByName(String name);
}