package com.porfolio.gravity.application.port.in.auth;

public record LoginCommand(String email, String password) {
}