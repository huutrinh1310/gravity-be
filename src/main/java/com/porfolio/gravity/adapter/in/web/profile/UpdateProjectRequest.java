package com.porfolio.gravity.adapter.in.web.profile;

import com.porfolio.gravity.domain.model.Skill;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
public record UpdateProjectRequest(@NotBlank String name, String description, List<Skill> skills) { }
