package com.porfolio.gravity.adapter.out.persistence.auth;

import com.porfolio.gravity.adapter.out.persistence.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_refresh_tokens_token", columnList = "token", unique = true),
        @Index(name = "idx_refresh_tokens_family", columnList = "token_family")
})
public class RefreshTokenJpaEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 36)
    String token;

    @Column(name = "token_family", nullable = false, length = 36)
    String tokenFamily;

    @Column(name = "profile_id", nullable = false)
    Integer profileId;

    @Column(name = "expires_at", nullable = false)
    Instant expiresAt;

    @Column(nullable = false)
    boolean revoked;
}