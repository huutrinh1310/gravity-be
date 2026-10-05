package com.porfolio.gravity.application.port.in.banner;

public record UpdateBannerCommand(Integer profileId, String title, String subtitle, String imageUrl, String linkUrl, Boolean active, Integer sortOrder) {
}
