package com.porfolio.gravity.application.port.in.profile;

import com.porfolio.gravity.domain.model.PortfolioTemplate;

public record CreatePortfolioCommand(Integer profileId, String name, String domainUrl, PortfolioTemplate template,
                                     String description, String imageUrl, Boolean isPublic,
                                     Boolean isIntegrateAnalytics) {
}
