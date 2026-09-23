package com.porfolio.gravity.application.port.in.profile;

public record UpdateProfileCommand(String name, String email, String address, String phone) {
}
