package com.porfolio.gravity.application.port.in.profile;

import com.porfolio.gravity.domain.model.Skill;
import java.util.List;

public record UpdateProjectCommand(String name, String description, List<Skill> skills) {
}
