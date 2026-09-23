package com.porfolio.gravity.adapter.in.web.profile;

import com.porfolio.gravity.domain.model.Skill;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ProjectRequest(@NotBlank String name, String description, List<Skill> skills, @NotNull Integer profileId) { }
