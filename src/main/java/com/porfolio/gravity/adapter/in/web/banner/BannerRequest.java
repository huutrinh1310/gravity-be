package com.porfolio.gravity.adapter.in.web.banner;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BannerRequest(
        @NotNull(message = "Banner profile ID is required") Integer profileId,
        @NotBlank(message = "Banner title is required") String title,
        String subtitle,
        @NotBlank(message = "Banner image URL is required") String imageUrl,
        String linkUrl,
        Boolean active,
        Integer sortOrder
) {
}
