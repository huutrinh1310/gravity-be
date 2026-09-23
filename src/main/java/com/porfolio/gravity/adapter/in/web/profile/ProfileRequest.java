package com.porfolio.gravity.adapter.in.web.profile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ProfileRequest(@NotBlank String name, @NotBlank @Email String email, @NotBlank String address, String phone) { }
