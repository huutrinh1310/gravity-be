package com.porfolio.gravity.application.port.in.auth;

public record RegisterCommand(String name, String email, String password, String address) {
}