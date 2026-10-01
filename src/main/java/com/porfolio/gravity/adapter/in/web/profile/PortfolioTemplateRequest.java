package com.porfolio.gravity.adapter.in.web.profile;

import com.porfolio.gravity.domain.model.PortfolioTemplate;
import com.porfolio.gravity.domain.model.PortfolioType;
import jakarta.validation.constraints.NotBlank;

public record PortfolioTemplateRequest(Integer id, @NotBlank String name, String description, String imageUrl, String type) {
    public PortfolioTemplate toDomain() {
        return new PortfolioTemplate(id, name, description, imageUrl,
                type == null ? null : new PortfolioType(type));
    }
}
