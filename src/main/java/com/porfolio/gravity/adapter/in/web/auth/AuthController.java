package com.porfolio.gravity.adapter.in.web.auth;

import com.porfolio.gravity.application.port.in.auth.AuthResult;
import com.porfolio.gravity.application.port.in.auth.AuthUseCase;
import com.porfolio.gravity.application.port.in.auth.LoginCommand;
import com.porfolio.gravity.application.port.in.auth.RegisterCommand;
import com.porfolio.gravity.domain.exception.AuthenticationException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@Tag(name = "Authentication", description = "Account authentication and session management")
@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final String REFRESH_COOKIE_NAME = "refresh_token";
    private static final String REFRESH_COOKIE_PATH = "/api/v1/auth/refresh";

    private final AuthUseCase authUseCase;
    private final boolean cookieSecure;
    private final long refreshTokenExpirationMs;

    public AuthController(AuthUseCase authUseCase,
                          @Value("${app.auth.cookie-secure:false}") boolean cookieSecure,
                          @Value("${app.jwt.refresh-token-expiration-ms}") long refreshTokenExpirationMs) {
        this.authUseCase = authUseCase;
        this.cookieSecure = cookieSecure;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        AuthResult result = authUseCase.register(new RegisterCommand(request.name(), request.email(),
                request.password(), request.address()));
        setRefreshTokenCookie(response, result.refreshToken(), Duration.ofMillis(refreshTokenExpirationMs));
        return AuthResponse.from(result);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResult result = authUseCase.login(new LoginCommand(request.email(), request.password()));
        setRefreshTokenCookie(response, result.refreshToken(), Duration.ofMillis(refreshTokenExpirationMs));
        return AuthResponse.from(result);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshToken,
                                HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new AuthenticationException("Invalid refresh token");
        }
        AuthResult result = authUseCase.refresh(refreshToken);
        setRefreshTokenCookie(response, result.refreshToken(), Duration.ofMillis(refreshTokenExpirationMs));
        return AuthResponse.from(result);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshToken,
                       HttpServletResponse response) {
        authUseCase.logout(refreshToken);
        setRefreshTokenCookie(response, "", Duration.ZERO);
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String token, Duration maxAge) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path(REFRESH_COOKIE_PATH)
                .maxAge(maxAge)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}