package com.porfolio.gravity.adapter.out.persistence.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.time.Instant;
import java.util.Optional;

interface SpringDataRefreshTokenRepository extends JpaRepository<RefreshTokenJpaEntity, Long> {
    Optional<RefreshTokenJpaEntity> findByToken(String token);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select token from RefreshTokenJpaEntity token where token.token = :token")
    Optional<RefreshTokenJpaEntity> findByTokenForUpdate(@Param("token") String token);

    @Modifying
    @Query("update RefreshTokenJpaEntity token set token.revoked = true where token.tokenFamily = :tokenFamily")
    int revokeByTokenFamily(@Param("tokenFamily") String tokenFamily);

    long deleteAllByExpiresAtBefore(Instant now);
}