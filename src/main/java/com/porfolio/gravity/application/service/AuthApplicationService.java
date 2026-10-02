package com.porfolio.gravity.application.service;

import com.porfolio.gravity.application.port.in.auth.AuthResult;
import com.porfolio.gravity.application.port.in.auth.AuthUseCase;
import com.porfolio.gravity.application.port.in.auth.LoginCommand;
import com.porfolio.gravity.application.port.in.auth.RegisterCommand;
import com.porfolio.gravity.application.port.out.auth.RefreshTokenRepository;
import com.porfolio.gravity.application.port.out.profile.ProfileRepository;
import com.porfolio.gravity.configs.security.JwtTokenProvider;
import com.porfolio.gravity.domain.exception.AuthenticationException;
import com.porfolio.gravity.domain.model.Profile;
import com.porfolio.gravity.domain.model.RefreshToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@Transactional(noRollbackFor = AuthenticationException.class)
public class AuthApplicationService implements AuthUseCase {
    private static final String USER_ROLE = "ROLE_USER";

    private final ProfileRepository profileRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final long refreshTokenExpirationMs;

    public AuthApplicationService(ProfileRepository profileRepository,
                                  RefreshTokenRepository refreshTokenRepository,
                                  JwtTokenProvider tokenProvider,
                                  PasswordEncoder passwordEncoder,
                                  @Value("${app.jwt.refresh-token-expiration-ms}") long refreshTokenExpirationMs) {
        this.profileRepository = profileRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    @Override
    public AuthResult register(RegisterCommand command) {
        String email = normalizeEmail(command.email());
        if (profileRepository.findByEmail(email).isPresent()) {
            throw new AuthenticationException("Email is already registered");
        }

        Profile profile = Profile.createAuthenticated(command.name(), email, command.address(), null,
                passwordEncoder.encode(command.password()), "local", null, USER_ROLE);
        return issueTokenPair(profileRepository.save(profile));
    }

    @Override
    public AuthResult login(LoginCommand command) {
        Profile profile = profileRepository.findByEmail(normalizeEmail(command.email()))
                .orElseThrow(this::invalidCredentials);
        if (profile.passwordHash() == null || !passwordEncoder.matches(command.password(), profile.passwordHash())) {
            throw invalidCredentials();
        }
        return issueTokenPair(profile);
    }

    @Override
    public AuthResult refresh(String refreshToken) {
        RefreshToken current = refreshTokenRepository.findByTokenForUpdate(refreshToken)
                .orElseThrow(this::invalidCredentials);
        if (current.revoked()) {
            refreshTokenRepository.revokeByFamily(current.tokenFamily());
            throw invalidCredentials();
        }
        if (current.isExpired()) {
            throw invalidCredentials();
        }

        Profile profile = profileRepository.findById(current.profileId()).orElseThrow(this::invalidCredentials);
        RefreshToken next = current.rotate(refreshTokenExpirationMs);
        refreshTokenRepository.save(current);
        RefreshToken savedNext = refreshTokenRepository.save(next);
        String accessToken = tokenProvider.generateAccessToken(profile.id(), profile.email(), profile.role());
        return new AuthResult(accessToken, savedNext.token(), toProfileInfo(profile));
    }

    @Override
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(token -> refreshTokenRepository.revokeByFamily(token.tokenFamily()));
    }

    @Override
    public AuthResult oauthLogin(String provider, String providerUserId, String email, String name) {
        String normalizedProvider = provider == null ? "" : provider.toLowerCase(Locale.ROOT);
        if (!"google".equals(normalizedProvider) && !"github".equals(normalizedProvider)) {
            throw invalidCredentials();
        }
        if (providerUserId == null || providerUserId.isBlank() || email == null || email.isBlank()
                || name == null || name.isBlank()) {
            throw invalidCredentials();
        }

        String normalizedEmail = normalizeEmail(email);
        Profile profile = profileRepository.findByAuthProviderAndProviderId(normalizedProvider, providerUserId)
                .orElse(null);
        if (profile == null) {
            profile = profileRepository.findByEmail(normalizedEmail).orElse(null);
            if (profile == null) {
                profile = Profile.createAuthenticated(name, normalizedEmail, null, null,
                        null, normalizedProvider, providerUserId, USER_ROLE);
                profile = profileRepository.save(profile);
            } else {
                if (profile.providerId() != null && !normalizedProvider.equals(profile.authProvider())) {
                    throw invalidCredentials();
                }
                profile.linkOAuthProvider(normalizedProvider, providerUserId);
                profile = profileRepository.save(profile);
            }
        }
        return issueTokenPair(profile);
    }

    private AuthResult issueTokenPair(Profile profile) {
        String accessToken = tokenProvider.generateAccessToken(profile.id(), profile.email(), profile.role());
        RefreshToken refreshToken = refreshTokenRepository.save(
                RefreshToken.create(profile.id(), refreshTokenExpirationMs));
        return new AuthResult(accessToken, refreshToken.token(), toProfileInfo(profile));
        }

        private AuthResult.ProfileInfo toProfileInfo(Profile profile) {
        return new AuthResult.ProfileInfo(profile.id(), profile.name(), profile.email(), profile.role());
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private AuthenticationException invalidCredentials() {
        return new AuthenticationException("Invalid email or password");
    }
}