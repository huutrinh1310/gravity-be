package com.porfolio.gravity;

import com.porfolio.gravity.domain.exception.DomainValidationException;
import com.porfolio.gravity.domain.model.Portfolio;
import com.porfolio.gravity.domain.model.Profile;
import com.porfolio.gravity.domain.model.RefreshToken;
import com.porfolio.gravity.domain.model.Skill;
import com.porfolio.gravity.domain.exception.AuthenticationException;
import com.porfolio.gravity.shared.web.ApiResponse;
import com.porfolio.gravity.shared.web.GlobalExceptionHandler;
import com.porfolio.gravity.configs.security.JwtTokenProvider;
import com.porfolio.gravity.configs.security.JwtAuthenticationFilter;
import com.porfolio.gravity.application.port.in.auth.AuthResult;
import com.porfolio.gravity.adapter.in.web.auth.AuthResponse;
import com.porfolio.gravity.application.port.in.auth.LoginCommand;
import com.porfolio.gravity.application.port.in.auth.RegisterCommand;
import com.porfolio.gravity.application.port.out.auth.RefreshTokenRepository;
import com.porfolio.gravity.application.port.out.profile.ProfileRepository;
import com.porfolio.gravity.application.service.AuthApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class GravityApplicationTests {
    @Test
    void profileOwnsAndValidatesItsPortfolioItems() {
        Profile profile = Profile.create("Ada", "ada@example.com", "London", null);
        profile.addSkill("Java");
        profile.addProject("Gravity", "Portfolio API", List.of(Skill.create("java")));
        Portfolio portfolio = profile.addPortfolio("Gravity", "gravity.example", null, "Portfolio site",
                "https://example.com/image.png", true, false);

        assertEquals("Java", profile.skills().get(0).name());
        assertEquals("java", profile.projects().get(0).skills().get(0).name());
        assertEquals("Gravity", profile.portfolios().get(0).name());
        assertTrue(portfolio.isPublic());
        profile.changePortfolio(portfolio.id(), "Gravity updated", null, null, null, null, false, null);
        assertEquals("Gravity updated", profile.findPortfolio(portfolio.id()).name());
        profile.removePortfolio(portfolio.id());
        assertTrue(profile.portfolios().isEmpty());
        assertThrows(DomainValidationException.class, () -> profile.changeDetails("", "ada@example.com", "London", null));
        assertThrows(DomainValidationException.class, () -> profile.changeDetails("Ada", "not-an-email", "London", null));
        assertThrows(DomainValidationException.class,
                () -> profile.addPortfolio(" ", null, null, null, null, null, null));
    }

    @Test
    void profileCanRepresentLocalAndOAuthAuthentication() {
        Profile local = Profile.createAuthenticated("Ada", "ada@example.com", "London", null,
                "password-hash", "local", null, "ROLE_USER");
        Profile oauth = Profile.createAuthenticated("Grace", "grace@example.com", "London", null,
                null, "google", "google-user-123", "ROLE_USER");

        assertEquals("password-hash", local.passwordHash());
        assertEquals("local", local.authProvider());
        assertEquals("ROLE_USER", local.role());
        assertNull(oauth.passwordHash());
        assertEquals("google", oauth.authProvider());
        assertEquals("google-user-123", oauth.providerId());
    }

        @Test
        void oauthProfileMayOmitAddressButLocalProfileMayNot() {
                Profile oauth = Profile.createAuthenticated("Grace", "grace@example.com", null, null,
                                null, "google", "google-user-123", "ROLE_USER");

                assertNull(oauth.address());
                assertThrows(DomainValidationException.class, () -> Profile.createAuthenticated("Ada", "ada@example.com",
                                null, null, "password-hash", "local", null, "ROLE_USER"));
        }

        @Test
        void refreshTokenRotationCreatesNewTokenInSameFamilyAndRevokesPreviousToken() {
                RefreshToken original = RefreshToken.create(7, 60_000);

                RefreshToken rotated = original.rotate(60_000);

                assertNotEquals(original.token(), rotated.token());
                assertEquals(original.tokenFamily(), rotated.tokenFamily());
                assertEquals(original.profileId(), rotated.profileId());
                assertTrue(original.revoked());
                assertFalse(rotated.revoked());
                assertTrue(RefreshToken.create(7, 0).isExpired());
        }

        @Test
        void jwtTokenProviderSignsExpectedClaimsAndRejectsTampering() {
                JwtTokenProvider tokenProvider = new JwtTokenProvider("01234567890123456789012345678901", 900_000);

                String token = tokenProvider.generateAccessToken(7, "ada@example.com", "ROLE_USER");

                assertTrue(tokenProvider.isValid(token));
                assertEquals("7", tokenProvider.parseToken(token).getSubject());
                assertEquals("ada@example.com", tokenProvider.parseToken(token).get("email", String.class));
                assertEquals("ROLE_USER", tokenProvider.parseToken(token).get("role", String.class));
                assertFalse(tokenProvider.isValid(token + "tampered"));
        }

        @Test
        void jwtAuthenticationFilterDoesNotAuthenticateInvalidBearerTokens() throws Exception {
                JwtTokenProvider tokenProvider = new JwtTokenProvider("01234567890123456789012345678901", 900_000);
                JwtAuthenticationFilter filter = new JwtAuthenticationFilter(tokenProvider);
                MockHttpServletRequest request = new MockHttpServletRequest();
                request.addHeader("Authorization", "Bearer invalid-token");
                SecurityContextHolder.clearContext();

                filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

                assertNull(SecurityContextHolder.getContext().getAuthentication());
                SecurityContextHolder.clearContext();
        }

        @Test
        void registrationNormalizesEmailHashesPasswordAndPersistsTokenPair() {
                ProfileRepository profiles = mock(ProfileRepository.class);
                RefreshTokenRepository refreshTokens = mock(RefreshTokenRepository.class);
                JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
                PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
                when(profiles.findByEmail("ada@example.com")).thenReturn(java.util.Optional.empty());
                when(passwordEncoder.encode("Password123!")).thenReturn("password-hash");
                when(profiles.save(any(Profile.class))).thenAnswer(invocation -> {
                        Profile profile = invocation.getArgument(0);
                        profile.assignId(42);
                        return profile;
                });
                when(tokenProvider.generateAccessToken(42, "ada@example.com", "ROLE_USER")).thenReturn("access-token");
                when(refreshTokens.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
                AuthApplicationService service = new AuthApplicationService(profiles, refreshTokens, tokenProvider,
                                passwordEncoder, 604_800_000);

                AuthResult result = service.register(new RegisterCommand("Ada", " Ada@Example.com ",
                                "Password123!", "London"));

                assertEquals("access-token", result.accessToken());
                assertEquals("Ada", result.profile().name());
                assertEquals("ada@example.com", result.profile().email());
                assertEquals("ROLE_USER", result.profile().role());
                assertNotNull(result.refreshToken());
        }

        @Test
        void loginVerifiesPasswordAndReturnsOnlyPublicProfileFields() {
                ProfileRepository profiles = mock(ProfileRepository.class);
                RefreshTokenRepository refreshTokens = mock(RefreshTokenRepository.class);
                JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
                PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
                Profile profile = Profile.createAuthenticated("Ada", "ada@example.com", "London", null,
                                "password-hash", "local", null, "ROLE_USER");
                profile.assignId(42);
                when(profiles.findByEmail("ada@example.com")).thenReturn(java.util.Optional.of(profile));
                when(passwordEncoder.matches("Password123!", "password-hash")).thenReturn(true);
                when(tokenProvider.generateAccessToken(42, "ada@example.com", "ROLE_USER")).thenReturn("access-token");
                when(refreshTokens.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
                AuthApplicationService service = new AuthApplicationService(profiles, refreshTokens, tokenProvider,
                                passwordEncoder, 604_800_000);

                AuthResult result = service.login(new LoginCommand("ADA@example.com", "Password123!"));

                assertEquals("access-token", result.accessToken());
                assertEquals("ada@example.com", result.profile().email());
                assertEquals("ROLE_USER", result.profile().role());
        }

            @Test
            void loginRejectsWrongPasswordWithoutIssuingTokens() {
                ProfileRepository profiles = mock(ProfileRepository.class);
                RefreshTokenRepository refreshTokens = mock(RefreshTokenRepository.class);
                JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
                PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
                Profile profile = Profile.createAuthenticated("Ada", "ada@example.com", "London", null,
                        "password-hash", "local", null, "ROLE_USER");
                when(profiles.findByEmail("ada@example.com")).thenReturn(java.util.Optional.of(profile));
                when(passwordEncoder.matches("wrong-password", "password-hash")).thenReturn(false);
                AuthApplicationService service = new AuthApplicationService(profiles, refreshTokens, tokenProvider,
                        passwordEncoder, 604_800_000);

                assertThrows(AuthenticationException.class,
                        () -> service.login(new LoginCommand("ada@example.com", "wrong-password")));

                verifyNoInteractions(refreshTokens, tokenProvider);
            }

        @Test
        void reusingRevokedRefreshTokenRevokesItsFamily() {
                ProfileRepository profiles = mock(ProfileRepository.class);
                RefreshTokenRepository refreshTokens = mock(RefreshTokenRepository.class);
                JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
                PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
                RefreshToken revokedToken = RefreshToken.restore(1L, "reused-token", "family-1", 42,
                                Instant.now().plusSeconds(300), true);
                when(refreshTokens.findByTokenForUpdate("reused-token")).thenReturn(java.util.Optional.of(revokedToken));
                AuthApplicationService service = new AuthApplicationService(profiles, refreshTokens, tokenProvider,
                                passwordEncoder, 604_800_000);

                assertThrows(AuthenticationException.class, () -> service.refresh("reused-token"));

                verify(refreshTokens).revokeByFamily("family-1");
        }

            @Test
            void refreshRotatesAnActiveTokenAndIssuesANewPair() {
                ProfileRepository profiles = mock(ProfileRepository.class);
                RefreshTokenRepository refreshTokens = mock(RefreshTokenRepository.class);
                JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
                PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
                Profile profile = Profile.createAuthenticated("Ada", "ada@example.com", "London", null,
                        "password-hash", "local", null, "ROLE_USER");
                profile.assignId(42);
                RefreshToken activeToken = RefreshToken.restore(1L, "old-refresh", "family-1", 42,
                        Instant.now().plusSeconds(300), false);
                when(refreshTokens.findByTokenForUpdate("old-refresh")).thenReturn(java.util.Optional.of(activeToken));
                when(profiles.findById(42)).thenReturn(java.util.Optional.of(profile));
                when(refreshTokens.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
                when(tokenProvider.generateAccessToken(42, "ada@example.com", "ROLE_USER")).thenReturn("new-access");
                AuthApplicationService service = new AuthApplicationService(profiles, refreshTokens, tokenProvider,
                        passwordEncoder, 604_800_000);

                AuthResult result = service.refresh("old-refresh");

                assertTrue(activeToken.revoked());
                assertNotEquals("old-refresh", result.refreshToken());
                assertEquals("new-access", result.accessToken());
                verify(refreshTokens, times(2)).save(any(RefreshToken.class));
            }

            @Test
            void expiredRefreshTokenIsRejectedWithoutRotation() {
                ProfileRepository profiles = mock(ProfileRepository.class);
                RefreshTokenRepository refreshTokens = mock(RefreshTokenRepository.class);
                JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
                PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
                RefreshToken expiredToken = RefreshToken.restore(1L, "expired-refresh", "family-1", 42,
                        Instant.now().minusSeconds(1), false);
                when(refreshTokens.findByTokenForUpdate("expired-refresh")).thenReturn(java.util.Optional.of(expiredToken));
                AuthApplicationService service = new AuthApplicationService(profiles, refreshTokens, tokenProvider,
                        passwordEncoder, 604_800_000);

                assertThrows(AuthenticationException.class, () -> service.refresh("expired-refresh"));

                verify(refreshTokens, never()).save(any(RefreshToken.class));
                verifyNoInteractions(tokenProvider);
            }

            @Test
            void logoutRevokesTheRefreshTokenFamily() {
                ProfileRepository profiles = mock(ProfileRepository.class);
                RefreshTokenRepository refreshTokens = mock(RefreshTokenRepository.class);
                JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
                PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
                RefreshToken token = RefreshToken.restore(1L, "refresh-token", "family-1", 42,
                        Instant.now().plusSeconds(300), false);
                when(refreshTokens.findByToken("refresh-token")).thenReturn(java.util.Optional.of(token));
                AuthApplicationService service = new AuthApplicationService(profiles, refreshTokens, tokenProvider,
                        passwordEncoder, 604_800_000);

                service.logout("refresh-token");

                verify(refreshTokens).revokeByFamily("family-1");
            }

        @Test
        void oauthLoginCreatesAddresslessProfileWithoutPassword() {
                ProfileRepository profiles = mock(ProfileRepository.class);
                RefreshTokenRepository refreshTokens = mock(RefreshTokenRepository.class);
                JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
                PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
                when(profiles.findByAuthProviderAndProviderId("google", "google-user-123"))
                                .thenReturn(java.util.Optional.empty());
                when(profiles.findByEmail("grace@example.com")).thenReturn(java.util.Optional.empty());
                when(profiles.save(any(Profile.class))).thenAnswer(invocation -> {
                        Profile profile = invocation.getArgument(0);
                        profile.assignId(43);
                        return profile;
                });
                when(tokenProvider.generateAccessToken(43, "grace@example.com", "ROLE_USER")).thenReturn("access-token");
                when(refreshTokens.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
                AuthApplicationService service = new AuthApplicationService(profiles, refreshTokens, tokenProvider,
                                passwordEncoder, 604_800_000);

                AuthResult result = service.oauthLogin("google", "google-user-123", "Grace@Example.com", "Grace");

                assertEquals("grace@example.com", result.profile().email());
                assertEquals("ROLE_USER", result.profile().role());
                assertNotNull(result.refreshToken());
        }

            @Test
            void oauthLoginReusesAnExistingProviderIdentity() {
                ProfileRepository profiles = mock(ProfileRepository.class);
                RefreshTokenRepository refreshTokens = mock(RefreshTokenRepository.class);
                JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
                PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
                Profile profile = Profile.createAuthenticated("Grace", "grace@example.com", null, null,
                        null, "google", "google-user-123", "ROLE_USER");
                profile.assignId(43);
                when(profiles.findByAuthProviderAndProviderId("google", "google-user-123"))
                        .thenReturn(java.util.Optional.of(profile));
                when(tokenProvider.generateAccessToken(43, "grace@example.com", "ROLE_USER")).thenReturn("access-token");
                when(refreshTokens.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
                AuthApplicationService service = new AuthApplicationService(profiles, refreshTokens, tokenProvider,
                        passwordEncoder, 604_800_000);

                AuthResult result = service.oauthLogin("google", "google-user-123", "grace@example.com", "Grace");

                assertEquals(43, result.profile().id());
                verify(profiles, never()).save(any(Profile.class));
            }

            @Test
            void oauthLoginLinksAProviderToAnExistingLocalProfile() {
                ProfileRepository profiles = mock(ProfileRepository.class);
                RefreshTokenRepository refreshTokens = mock(RefreshTokenRepository.class);
                JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
                PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
                Profile profile = Profile.createAuthenticated("Grace", "grace@example.com", "London", null,
                        "password-hash", "local", null, "ROLE_USER");
                profile.assignId(43);
                when(profiles.findByAuthProviderAndProviderId("github", "github-user-123"))
                        .thenReturn(java.util.Optional.empty());
                when(profiles.findByEmail("grace@example.com")).thenReturn(java.util.Optional.of(profile));
                when(profiles.save(profile)).thenReturn(profile);
                when(tokenProvider.generateAccessToken(43, "grace@example.com", "ROLE_USER")).thenReturn("access-token");
                when(refreshTokens.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
                AuthApplicationService service = new AuthApplicationService(profiles, refreshTokens, tokenProvider,
                        passwordEncoder, 604_800_000);

                service.oauthLogin("github", "github-user-123", "Grace@example.com", "Grace");

                assertEquals("github", profile.authProvider());
                assertEquals("github-user-123", profile.providerId());
            }

        @Test
        void authResponseNeverContainsRefreshToken() {
                AuthResult result = new AuthResult("access-token", "refresh-secret",
                                new AuthResult.ProfileInfo(43, "Grace", "grace@example.com", "ROLE_USER"));

                AuthResponse response = AuthResponse.from(result);

                assertEquals("access-token", response.accessToken());
                assertEquals("grace@example.com", response.profile().email());
                assertFalse(response.toString().contains("refresh-secret"));
        }

        @Test
        void authenticationErrorsUseGeneric401And403Responses() {
                GlobalExceptionHandler handler = new GlobalExceptionHandler();
                MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");

                var unauthorized = handler.handleAuthenticationException(new AuthenticationException("sensitive detail"), request);
                var forbidden = handler.handleAccessDenied(new AccessDeniedException("sensitive detail"), request);

                assertEquals(401, unauthorized.getStatusCode().value());
                assertEquals("Authentication failed", unauthorized.getBody().getMessage());
                assertEquals(403, forbidden.getStatusCode().value());
                assertEquals("Access denied", forbidden.getBody().getMessage());
        }
}
