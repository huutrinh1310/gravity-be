package com.porfolio.gravity.domain.model;

import java.time.Instant;
import java.util.UUID;

public final class RefreshToken {
    private Long id;
    private final String token;
    private final String tokenFamily;
    private final Integer profileId;
    private final Instant expiresAt;
    private boolean revoked;

    private RefreshToken(String token, String tokenFamily, Integer profileId, Instant expiresAt, boolean revoked) {
        this.token = token;
        this.tokenFamily = tokenFamily;
        this.profileId = profileId;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
    }

    public static RefreshToken create(Integer profileId, long expirationMs) {
        return new RefreshToken(UUID.randomUUID().toString(), UUID.randomUUID().toString(), profileId,
                Instant.now().plusMillis(expirationMs), false);
    }

    public static RefreshToken restore(Long id, String token, String tokenFamily, Integer profileId,
                                       Instant expiresAt, boolean revoked) {
        RefreshToken refreshToken = new RefreshToken(token, tokenFamily, profileId, expiresAt, revoked);
        refreshToken.id = id;
        return refreshToken;
    }

    public RefreshToken rotate(long expirationMs) {
        revoked = true;
        return new RefreshToken(UUID.randomUUID().toString(), tokenFamily, profileId,
                Instant.now().plusMillis(expirationMs), false);
    }

    public boolean isExpired() {
        return !Instant.now().isBefore(expiresAt);
    }

    public Long id() {
        return id;
    }

    public String token() {
        return token;
    }

    public String tokenFamily() {
        return tokenFamily;
    }

    public Integer profileId() {
        return profileId;
    }

    public Instant expiresAt() {
        return expiresAt;
    }

    public boolean revoked() {
        return revoked;
    }
}