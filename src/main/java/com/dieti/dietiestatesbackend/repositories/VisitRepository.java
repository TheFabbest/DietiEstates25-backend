package com.dieti.dietiestatesbackend.repositories;

import com.dieti.dietiestatesbackend.dto.response.AgentVisitDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;

import com.dieti.dietiestatesbackend.entities.Visit;
import com.dieti.dietiestatesbackend.enums.VisitStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    // Gets all visits from the agent
    @Query(value = "SELECT new com.dieti.dietiestatesbackend.dto.response.AgentVisitDTO(" +
                   "v, " +
                   "v.property.propertyCategory.propertyType, " +
                   "new com.dieti.dietiestatesbackend.dto.response.AddressResponseDTO(" +
                   "v.property.address.id, " +
                   "v.property.address.country, " +
                   "v.property.address.province, " +
                   "v.property.address.city, " +
                   "v.property.address.street, " +
                   "v.property.address.streetNumber, " +
                   "v.property.address.building, " +
                   "v.property.address.coordinates.latitude, " +
                   "v.property.address.coordinates.longitude), " +
                   "new com.dieti.dietiestatesbackend.dto.response.UserResponse(" +
                   "v.user.id, " +
                   "CONCAT(v.user.name, ' ', v.user.surname), " +
                   "v.user.email)) " +
                   "FROM Visit v WHERE v.property.agent.id = :agentID",
           countQuery = "SELECT count(v) FROM Visit v WHERE v.property.agent.id = :agentID")
    Page<AgentVisitDTO> getAgentVisits(@Param("agentID") Long agentID, Pageable pageable);

    // Gets all visits requested by a user
    @Query(value = "SELECT new com.dieti.dietiestatesbackend.dto.response.AgentVisitDTO(" +
                   "v, " +
                   "v.property.propertyCategory.propertyType, " +
                   "new com.dieti.dietiestatesbackend.dto.response.AddressResponseDTO(" +
                   "v.property.address.id, " +
                   "v.property.address.country, " +
                   "v.property.address.province, " +
                   "v.property.address.city, " +
                   "v.property.address.street, " +
                   "v.property.address.streetNumber, " +
                   "v.property.address.building, " +
                   "v.property.address.coordinates.latitude, " +
                   "v.property.address.coordinates.longitude)) " +
                   "FROM Visit v WHERE v.user.id = :userId",
           countQuery = "SELECT count(v) FROM Visit v WHERE v.user.id = :userId")
    Page<AgentVisitDTO> getUserVisits(@Param("userId") Long userId, Pageable pageable);

    // Gets all visits associated to a property
    @Query(value = "SELECT new com.dieti.dietiestatesbackend.dto.response.AgentVisitDTO(" +
                   "v, " +
                   "v.property.propertyCategory.propertyType, " +
                   "new com.dieti.dietiestatesbackend.dto.response.AddressResponseDTO(" +
                   "v.property.address.id, " +
                   "v.property.address.country, " +
                   "v.property.address.province, " +
                   "v.property.address.city, " +
                   "v.property.address.street, " +
                   "v.property.address.streetNumber, " +
                   "v.property.address.building, " +
                   "v.property.address.coordinates.latitude, " +
                   "v.property.address.coordinates.longitude)) " +
                   "FROM Visit v WHERE v.property.id = :propertyId",
           countQuery = "SELECT count(v) FROM Visit v WHERE v.property.id = :propertyId")
    Page<AgentVisitDTO> getPropertyVisits(@Param("propertyId") Long propertyId, Pageable pageable);

    @Query("SELECT new com.dieti.dietiestatesbackend.dto.response.AgentVisitDTO(" +
          "v, " +
          "v.property.propertyCategory.propertyType, " +
          "new com.dieti.dietiestatesbackend.dto.response.AddressResponseDTO(" +
          "v.property.address.id, " +
          "v.property.address.country, " +
          "v.property.address.province, " +
          "v.property.address.city, " +
          "v.property.address.street, " +
          "v.property.address.streetNumber, " +
          "v.property.address.building, " +
          "v.property.address.coordinates.latitude, " +
          "v.property.address.coordinates.longitude)) " +
          "FROM Visit v WHERE v.id = :visitId")
    Optional<AgentVisitDTO> findAgentVisitById(@Param("visitId") Long visitId);

    /*
     * NOTE su duplicazione query (con e senza LOCK)
     *
     * Spring Data JPA non permette di applicare @Lock dinamicamente a runtime né di
     * parametrizzare l'annotazione @Lock usando espressioni JPQL. Perciò manteniamo
     * due metodi con la stessa JPQL: uno senza @Lock (uso lettura) e uno con
     * @Lock(LockModeType.PESSIMISTIC_WRITE) (uso in contesti di conferma/booking).
     *
     * Rationale:
     * - Evita race condition quando si conferma una visita (usare il metodo con lock).
     * - Permette letture non bloccanti quando il locking pessimista non è necessario.
     *
     * Se in futuro si desidera eliminare la duplicazione, considerare l'introduzione
     * di un'implementazione custom del repository (VisitRepositoryCustom + VisitRepositoryImpl)
     * che usi EntityManager per applicare il lock programmaticamente.
     */
 
    // Find confirmed visits for a user that overlap with the given interval (no lock)
    @Query("SELECT v FROM Visit v WHERE v.user.id = :userId AND v.status = :status AND NOT (v.endTime <= :startTime OR v.startTime >= :endTime)")
    List<Visit> findOverlappingVisitsForUser(@Param("userId") Long userId,
                                            @Param("startTime") Instant startTime,
                                            @Param("endTime") Instant endTime,
                                            @Param("status") VisitStatus status);
 
    // Same query with pessimistic lock to avoid race conditions during booking
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Visit v WHERE v.user.id = :userId AND v.status = :status AND NOT (v.endTime <= :startTime OR v.startTime >= :endTime)")
    List<Visit> findOverlappingVisitsForUserWithLock(@Param("userId") Long userId,
                                                     @Param("startTime") Instant startTime,
                                                     @Param("endTime") Instant endTime,
                                                     @Param("status") VisitStatus status);
 
    // Find confirmed visits for an agent that overlap with the given interval (no lock)
    @Query("SELECT v FROM Visit v WHERE v.agent.id = :agentId AND v.status = :status AND NOT (v.endTime <= :startTime OR v.startTime >= :endTime)")
    List<Visit> findOverlappingVisitsForAgent(@Param("agentId") Long agentId,
                                              @Param("startTime") Instant startTime,
                                              @Param("endTime") Instant endTime,
                                              @Param("status") VisitStatus status);
 
    // Same query with pessimistic lock to avoid race conditions during booking
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Visit v WHERE v.agent.id = :agentId AND v.status = :status AND NOT (v.endTime <= :startTime OR v.startTime >= :endTime)")
    List<Visit> findOverlappingVisitsForAgentWithLock(@Param("agentId") Long agentId,
                                                      @Param("startTime") Instant startTime,
                                                      @Param("endTime") Instant endTime,
                                                      @Param("status") VisitStatus status);

    // --- NEW: counts used by overbooking logic ---

    /**
     * Conta il numero di visite confermate per una specifica proprietà che si sovrappongono all'intervallo dato.
     * Usa LOCK pessimista per evitare race condition durante la conferma della visita.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT COUNT(v) FROM Visit v WHERE v.property.id = :propertyId AND v.status = :status AND NOT (v.endTime <= :startTime OR v.startTime >= :endTime)")
    long countConfirmedVisitsForPropertyWithLock(@Param("propertyId") Long propertyId,
                                                 @Param("startTime") Instant startTime,
                                                 @Param("endTime") Instant endTime,
                                                 @Param("status") VisitStatus status);

    /**
     * Conta il numero di proprietà distinte per le quali l'agente ha visite confermate
     * che si sovrappongono all'intervallo dato, escludendo la proprietà corrente.
     * Usa LOCK pessimista per evitare race condition durante la conferma della visita.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT COUNT(DISTINCT v.property.id) FROM Visit v WHERE v.agent.id = :agentId AND v.status = :status AND v.property.id <> :propertyId AND NOT (v.endTime <= :startTime OR v.startTime >= :endTime)")
    long countDistinctConfirmedPropertiesForAgentWithLock(@Param("agentId") Long agentId,
                                                          @Param("propertyId") Long propertyId,
                                                          @Param("startTime") Instant startTime,
                                                          @Param("endTime") Instant endTime,
                                                          @Param("status") VisitStatus status);
}