package com.porfolio.gravity.adapter.out.persistence.auth;

import com.porfolio.gravity.application.port.out.auth.RefreshTokenRepository;
import com.porfolio.gravity.domain.model.RefreshToken;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Repository
@Transactional
public class RefreshTokenPersistenceAdapter implements RefreshTokenRepository {
    private final SpringDataRefreshTokenRepository repository;

    public RefreshTokenPersistenceAdapter(SpringDataRefreshTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        return toDomain(repository.save(toEntity(token)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return repository.findByToken(token).map(this::toDomain);
    }

    @Override
    public Optional<RefreshToken> findByTokenForUpdate(String token) {
        return repository.findByTokenForUpdate(token).map(this::toDomain);
    }

    @Override
    public void revokeByFamily(String tokenFamily) {
        repository.revokeByTokenFamily(tokenFamily);
    }

    @Override
    public void deleteExpiredTokens() {
        repository.deleteAllByExpiresAtBefore(Instant.now());
    }

    private RefreshTokenJpaEntity toEntity(RefreshToken source) {
        RefreshTokenJpaEntity target = new RefreshTokenJpaEntity();
        target.id = source.id();
        target.token = source.token();
        target.tokenFamily = source.tokenFamily();
        target.profileId = source.profileId();
        target.expiresAt = source.expiresAt();
        target.revoked = source.revoked();
        return target;
    }

    private RefreshToken toDomain(RefreshTokenJpaEntity source) {
        return RefreshToken.restore(source.id, source.token, source.tokenFamily, source.profileId,
                source.expiresAt, source.revoked);
    }
}