package com.porfolio.gravity.application.port.in.banner;

import com.porfolio.gravity.domain.model.Banner;

import java.util.List;

public interface BannerUseCase {
    List<Banner> listBanners();

    Banner getBanner(Integer id);

    Banner createBanner(CreateBannerCommand command);

    Banner updateBanner(Integer id, UpdateBannerCommand command);

    void deleteBanner(Integer id);

    Banner getBannerByProfile(Integer profileId);
}
