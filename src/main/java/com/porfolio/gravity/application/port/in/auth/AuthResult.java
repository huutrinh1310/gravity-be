package com.porfolio.gravity.application.port.in.auth;

public record AuthResult(String accessToken, String refreshToken, ProfileInfo profile) {
    public record ProfileInfo(Integer id, String name, String email, String role) {
    }
}