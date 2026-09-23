package com.porfolio.gravity.adapter.in.web.banner;

import com.porfolio.gravity.domain.model.Banner;

public record BannerResponse(
        Integer id,
        String title,
        String subtitle,
        String imageUrl,
        String linkUrl,
        boolean active,
        Integer sortOrder
) {
    public static BannerResponse from(Banner banner) {
        return new BannerResponse(
                banner.id(),
                banner.title(),
                banner.subtitle(),
                banner.imageUrl(),
                banner.linkUrl(),
                banner.active(),
                banner.sortOrder()
        );
    }
}
