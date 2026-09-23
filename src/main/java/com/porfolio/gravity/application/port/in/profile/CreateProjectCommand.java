package com.porfolio.gravity.application.port.in.profile;

import com.porfolio.gravity.domain.model.Skill;
import java.util.List;

public record CreateProjectCommand(Integer profileId, String name, String description, List<Skill> skills) {
}
