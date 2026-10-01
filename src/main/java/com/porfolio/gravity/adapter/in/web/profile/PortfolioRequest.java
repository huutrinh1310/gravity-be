package com.porfolio.gravity.adapter.in.web.profile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PortfolioRequest(
        @NotNull Integer profileId,
        @NotBlank String name,
        String domainUrl,
        @Valid PortfolioTemplateRequest template,
        String description,
        String imageUrl,
        Boolean isPublic,
        Boolean isIntegrateAnalytics) {
}
