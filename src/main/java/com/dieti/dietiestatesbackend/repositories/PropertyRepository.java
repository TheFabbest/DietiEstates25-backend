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
     * Ricerca per keyword sulla description con fetch delle relazioni necessarie.
     */
    @Query("""
        select p from Property p
          left join fetch p.contract
          left join fetch p.propertyCategory
          left join fetch p.agent
          left join fetch p.address
          left join fetch treat(p as ResidentialProperty).heating
        where lower(p.description) like lower(concat('%', :keyword, '%'))
        """)
    List<Property> searchByDescription(@Param("keyword") String keyword);

    /**
     * Featured (ultime create) con fetch relazioni.
     * Uso di countQuery separata senza fetch join per la paginazione.
     */
    @Query(value = """
        select p from Property p
          left join fetch p.contract
          left join fetch p.propertyCategory
          left join fetch p.agent
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
          left join fetch p.agent
          left join fetch p.address
          left join fetch treat(p as ResidentialProperty).heating
        where p.id = :id
        """)
    Optional<Property> findDetailedById(@Param("id") Long id);
}