package com.porfolio.gravity.adapter.in.web.profile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record UpdatePortfolioRequest(
        @NotBlank String name,
        String domainUrl,
        @Valid PortfolioTemplateRequest template,
        String description,
        String imageUrl,
        Boolean isPublic,
        Boolean isIntegrateAnalytics) {
}
