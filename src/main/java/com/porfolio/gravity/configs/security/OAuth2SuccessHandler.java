package com.porfolio.gravity.configs.security;

import com.porfolio.gravity.application.port.in.auth.AuthResult;
import com.porfolio.gravity.application.port.in.auth.AuthUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final AuthUseCase authUseCase;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final String frontendRedirectUri;
    private final boolean cookieSecure;
    private final long refreshTokenExpirationMs;
    private final RestClient restClient = RestClient.create();

    public OAuth2SuccessHandler(AuthUseCase authUseCase,
                                OAuth2AuthorizedClientService authorizedClientService,
                                @Value("${app.oauth2.frontend-redirect-uri}") String frontendRedirectUri,
                                @Value("${app.auth.cookie-secure:false}") boolean cookieSecure,
                                @Value("${app.jwt.refresh-token-expiration-ms}") long refreshTokenExpirationMs) {
        this.authUseCase = authUseCase;
        this.authorizedClientService = authorizedClientService;
        this.frontendRedirectUri = frontendRedirectUri;
        this.cookieSecure = cookieSecure;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        try {
            OAuth2AuthenticationToken oauthAuthentication = (OAuth2AuthenticationToken) authentication;
            String provider = oauthAuthentication.getAuthorizedClientRegistrationId();
            OAuth2User user = oauthAuthentication.getPrincipal();
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                    provider, oauthAuthentication.getName());
            if (client == null) {
                throw new IllegalArgumentException("OAuth client is unavailable");
            }

            Map<String, Object> attributes = user.getAttributes();
            String providerUserId = value(attributes, provider.equals("google") ? "sub" : "id");
            String email = verifiedEmail(provider, attributes, client.getAccessToken().getTokenValue());
            String name = value(attributes, "name");
            if ((name == null || name.isBlank()) && provider.equals("github")) {
                name = value(attributes, "login");
            }
            if (providerUserId == null || name == null || name.isBlank()) {
                throw new IllegalArgumentException("OAuth profile is incomplete");
            }

            AuthResult result = authUseCase.oauthLogin(provider, providerUserId, email, name);
            ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", result.refreshToken())
                    .httpOnly(true)
                    .secure(cookieSecure)
                    .sameSite("Lax")
                    .path("/api/v1/auth/refresh")
                    .maxAge(Duration.ofMillis(refreshTokenExpirationMs))
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            String redirectUri = UriComponentsBuilder.fromUriString(frontendRedirectUri)
                    .fragment("access_token=" + result.accessToken())
                    .build()
                    .toUriString();
            response.sendRedirect(redirectUri);
        } catch (RuntimeException exception) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "OAuth authentication failed");
        }
    }

    private String verifiedEmail(String provider, Map<String, Object> attributes, String accessToken) {
        if (provider.equals("google")) {
            if (!Boolean.TRUE.equals(attributes.get("email_verified"))) {
                throw new IllegalArgumentException("OAuth email is not verified");
            }
            return requiredValue(attributes, "email");
        }

        if (provider.equals("github")) {
            List<Map<String, Object>> emails = restClient.get()
                    .uri("https://api.github.com/user/emails")
                    .headers(headers -> {
                        headers.setBearerAuth(accessToken);
                        headers.setAccept(List.of(MediaType.valueOf("application/vnd.github+json")));
                        headers.set("X-GitHub-Api-Version", "2022-11-28");
                    })
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() { });
            if (emails != null) {
                return emails.stream()
                        .filter(email -> Boolean.TRUE.equals(email.get("primary")))
                        .filter(email -> Boolean.TRUE.equals(email.get("verified")))
                        .map(email -> email.get("email"))
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("OAuth email is not verified"));
            }
        }
        throw new IllegalArgumentException("OAuth provider is unsupported");
    }

    private String requiredValue(Map<String, Object> attributes, String key) {
        String value = value(attributes, key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("OAuth profile is incomplete");
        }
        return value;
    }

    private String value(Map<String, Object> attributes, String key) {
        Object value = attributes.get(key);
        return value == null ? null : value.toString();
    }
}