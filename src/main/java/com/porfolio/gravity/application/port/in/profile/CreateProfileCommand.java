package com.porfolio.gravity.application.port.in.profile;

public record CreateProfileCommand(String name, String email, String address, String phone) {
}
