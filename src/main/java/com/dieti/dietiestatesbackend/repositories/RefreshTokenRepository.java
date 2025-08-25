package com.dieti.dietiestatesbackend.repositories;

import com.dieti.dietiestatesbackend.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

/**
 * Repository per l'entità RefreshToken.
 * Si trova in un package denominato: com.dieti.dietiestatesbackend.repositories
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Query("select rt from RefreshToken rt join fetch rt.user where rt.tokenValue = :tokenValue")
    Optional<RefreshToken> findByTokenValue(@Param("tokenValue") String tokenValue);

    void deleteByTokenValue(String tokenValue);

    void deleteByUser_Id(Long userId);
}