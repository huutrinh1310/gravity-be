package com.porfolio.gravity.adapter.in.web.profile;

import jakarta.validation.constraints.NotBlank;
public record UpdateSkillRequest(@NotBlank String name) { }
