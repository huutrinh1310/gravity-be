package com.porfolio.gravity.adapter.in.web.auth;

import com.porfolio.gravity.application.port.in.auth.AuthResult;

public record AuthResponse(String accessToken, AuthResult.ProfileInfo profile) {
    public static AuthResponse from(AuthResult result) {
        return new AuthResponse(result.accessToken(), result.profile());
    }
}