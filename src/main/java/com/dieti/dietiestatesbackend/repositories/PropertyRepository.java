package com.dieti.dietiestatesbackend.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dieti.dietiestatesbackend.entities.Property;

/**
 * Repository ottimizzato per evitare N+1 caricando in eager (solo in lettura)
 * le relazioni necessarie alla serializzazione (mapper) tramite JOIN FETCH / TREAT.
 *
 * Relazioni caricate:
 *  - contract
 *  - propertyCategory
 *  - agent
 *  - address
 *  - heating (solo per ResidentialProperty)
 *
 * NOTA: Evitiamo di fetchare collezioni potenzialmente grandi (es. images se diventasse entità)
 * per non esplodere la cardinalità nelle paginazioni.
 */
@Repository
public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {

    /**
     * Metodo findAll con query JPQL custom per caricare tutte le relazioni necessarie
     * incluse le sottoclassi e le loro relazioni specifiche usando JOIN FETCH e TREAT.
     */
    @Query(value = "SELECT DISTINCT p FROM Property p " +
           "LEFT JOIN FETCH p.contract " +
           "LEFT JOIN FETCH p.propertyCategory " +
           "LEFT JOIN FETCH p.agent a " +
           "LEFT JOIN FETCH a.agency " +
           "LEFT JOIN FETCH p.address " +
           "LEFT JOIN FETCH TREAT(p AS ResidentialProperty).heating",
        countQuery = "select count(p) from Property p")
    Page<Property> findAllWithEagerFetch(Pageable pageable);

    /**
     * Search properties with filters and eager loading of all necessary relationships.
     * Combines specifications filtering with JOIN FETCH for eager loading.
     */
    @Query(value = "SELECT DISTINCT p FROM Property p " +
           "LEFT JOIN FETCH p.contract " +
           "LEFT JOIN FETCH p.propertyCategory " +
           "LEFT JOIN FETCH p.agent a " +
           "LEFT JOIN FETCH a.agency " +
           "LEFT JOIN FETCH p.address " +
           "LEFT JOIN FETCH TREAT(p AS ResidentialProperty).heating " +
           "JOIN p.address addr WHERE " +
           "addr.coordinates.latitude BETWEEN :minLat AND :maxLat AND " +
           "addr.coordinates.longitude BETWEEN :minLon AND :maxLon",
        countQuery = "select count(p) from Property p JOIN p.address addr WHERE " +
                     "addr.coordinates.latitude BETWEEN :minLat AND :maxLat AND " +
                     "addr.coordinates.longitude BETWEEN :minLon AND :maxLon")
    Page<Property> searchWithFiltersAndEagerFetch(
        @Param("minLat") double minLat,
        @Param("maxLat") double maxLat,
        @Param("minLon") double minLon,
        @Param("maxLon") double maxLon,
        Pageable pageable);


    /**
     * Featured (ultime create) con fetch relazioni.
     * Uso di countQuery separata senza fetch join per la paginazione.
     */
    @Query(value = """
        select p from Property p
          left join fetch p.contract
          left join fetch p.propertyCategory
          left join fetch p.agent a
          left join fetch a.agency
          left join fetch p.address
          left join fetch treat(p as ResidentialProperty).heating
        order by p.createdAt desc
        """,
        countQuery = "select count(p) from Property p")
    Page<Property> getFeatured(Pageable pageable);

    /**
     * Dettaglio singola property con fetch completo.
     */
    @Query("""
        select p from Property p
          left join fetch p.contract
          left join fetch p.propertyCategory
          left join fetch p.agent a
          left join fetch a.agency
          left join fetch p.address
          left join fetch treat(p as ResidentialProperty).heating
        where p.id = :id
        """)
    Optional<Property> findDetailedById(@Param("id") Long id);

    /**
     * Recupera un insieme di proprietà per una lista di ID con fetch delle relazioni necessarie.
     * Restituisce solo le proprietà esistenti (ignora gli ID non trovati).
     */
    @Query("""
        select distinct p from Property p
          left join fetch p.contract
          left join fetch p.propertyCategory
          left join fetch p.agent a
          left join fetch a.agency
          left join fetch p.address
          left join fetch treat(p as ResidentialProperty).heating
        where p.id in :ids
        """)
    List<Property> findAllDetailedByIdIn(@Param("ids") List<Long> ids);

    @Query(value = """
        select p from Property p
          left join fetch p.contract
          left join fetch p.propertyCategory
          left join fetch p.agent a
          left join fetch a.agency
          left join fetch p.address
          left join fetch treat(p as ResidentialProperty).heating
        where a.id = :agentID
        """,
        countQuery = "select count(p) from Property p where p.agent.id = :agentID")
    Page<Property> getPropertiesByAgentId(@Param("agentID") Long agentID, Pageable pageable);
}