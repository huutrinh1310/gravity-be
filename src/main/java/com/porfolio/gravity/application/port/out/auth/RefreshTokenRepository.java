package com.porfolio.gravity.application.port.out.auth;

import com.porfolio.gravity.domain.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken token);

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByTokenForUpdate(String token);

    void revokeByFamily(String tokenFamily);

    void deleteExpiredTokens();
}