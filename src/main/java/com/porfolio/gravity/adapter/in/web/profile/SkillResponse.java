package com.porfolio.gravity.adapter.in.web.profile;

import com.porfolio.gravity.domain.model.Skill;

public record SkillResponse(Integer id, String name) {
    public static SkillResponse from(Skill value) {
        return new SkillResponse(value.id(), value.name());
    }
}
