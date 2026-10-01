package com.porfolio.gravity.adapter.in.web.profile;

import com.porfolio.gravity.domain.model.Portfolio;

public record PortfolioResponse(Integer id, String name, String domainUrl, PortfolioTemplateResponse template,
                                String description, String imageUrl, Boolean isPublic,
                                Boolean isIntegrateAnalytics) {
    public static PortfolioResponse from(Portfolio value) {
        return new PortfolioResponse(value.id(), value.name(), value.domainUrl(),
                PortfolioTemplateResponse.from(value.template()), value.description(), value.imageUrl(),
                value.isPublic(), value.isIntegrateAnalytics());
    }
}
