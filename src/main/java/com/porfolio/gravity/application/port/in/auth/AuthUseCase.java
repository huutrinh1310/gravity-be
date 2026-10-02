package com.porfolio.gravity.application.port.in.auth;

public interface AuthUseCase {
    AuthResult register(RegisterCommand command);

    AuthResult login(LoginCommand command);

    AuthResult refresh(String refreshToken);

    void logout(String refreshToken);

    AuthResult oauthLogin(String provider, String providerUserId, String email, String name);
}