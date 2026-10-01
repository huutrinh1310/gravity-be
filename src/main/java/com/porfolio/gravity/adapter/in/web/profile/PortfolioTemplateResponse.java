package com.porfolio.gravity.adapter.in.web.profile;

import com.porfolio.gravity.domain.model.PortfolioTemplate;

public record PortfolioTemplateResponse(Integer id, String name, String description, String imageUrl, String type) {
    public static PortfolioTemplateResponse from(PortfolioTemplate template) {
        return template == null ? null : new PortfolioTemplateResponse(template.id(), template.name(),
                template.description(), template.imageUrl(),
                template.type() == null ? null : template.type().name());
    }
}
