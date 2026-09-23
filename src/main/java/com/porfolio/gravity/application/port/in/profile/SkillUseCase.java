package com.porfolio.gravity.application.port.in.profile;

import com.porfolio.gravity.domain.model.Skill;

import java.util.List;

/**
 * Inbound port for skills owned by a profile.
 */
public interface SkillUseCase {
    List<Skill> listSkills();

    Skill getSkill(Integer id);

    Skill createSkill(CreateSkillCommand command);

    Skill updateSkill(Integer id, UpdateSkillCommand command);

    void deleteSkill(Integer id);
}
