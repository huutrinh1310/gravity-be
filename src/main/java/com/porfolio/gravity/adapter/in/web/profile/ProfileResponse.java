package com.porfolio.gravity.adapter.in.web.profile;

import com.porfolio.gravity.domain.model.Profile;

import java.util.List;

public record ProfileResponse(Integer id, String name, String email, String address, String phone,
                              List<SkillResponse> skills, List<ProjectResponse> projects,
                              List<PortfolioResponse> portfolios) {
    public static ProfileResponse from(Profile value) {
        return new ProfileResponse(value.id(), value.name(), value.email(), value.address(), value.phone(),
                value.skills().stream().map(SkillResponse::from).toList(),
                value.projects().stream().map(ProjectResponse::from).toList(),
                value.portfolios().stream().map(PortfolioResponse::from).toList());
    }
}
