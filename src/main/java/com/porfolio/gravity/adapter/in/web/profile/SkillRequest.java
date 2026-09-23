package com.porfolio.gravity.adapter.in.web.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SkillRequest(@NotBlank String name, @NotNull Integer profileId) { }
