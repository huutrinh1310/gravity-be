package com.porfolio.gravity.adapter.in.web.profile;

import com.porfolio.gravity.domain.model.Project;
import com.porfolio.gravity.domain.model.Skill;
import java.util.List;
public record ProjectResponse(Integer id, String name, String description, List<Skill> skills) { public static ProjectResponse from(Project value) { return new ProjectResponse(value.id(), value.name(), value.description(), value.skills()); } }
