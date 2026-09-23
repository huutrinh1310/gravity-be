package com.porfolio.gravity.application.service;

import com.porfolio.gravity.application.port.in.banner.BannerUseCase;
import com.porfolio.gravity.application.port.in.banner.CreateBannerCommand;
import com.porfolio.gravity.application.port.in.banner.UpdateBannerCommand;
import com.porfolio.gravity.application.port.out.banner.BannerRepository;
import com.porfolio.gravity.domain.exception.BannerResourceNotFoundException;
import com.porfolio.gravity.domain.model.Banner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BannerApplicationService implements BannerUseCase {
    private final BannerRepository banners;

    public BannerApplicationService(BannerRepository banners) {
        this.banners = banners;
    }

    @Override
    public List<Banner> listBanners() {
        return banners.findAll();
    }

    @Override
    public Banner getBanner(Integer id) {
        return banners.findById(id).orElseThrow(() -> new BannerResourceNotFoundException("Banner with ID " + id + " not found"));
    }

    @Override
    @Transactional
    public Banner createBanner(CreateBannerCommand command) {
        return banners.save(Banner.create(command.title(), command.subtitle(), command.imageUrl(), command.linkUrl(), command.active(), command.sortOrder()));
    }

    @Override
    @Transactional
    public Banner updateBanner(Integer id, UpdateBannerCommand command) {
        Banner banner = getBanner(id);
        banner.changeDetails(command.title(), command.subtitle(), command.imageUrl(), command.linkUrl(), command.active(), command.sortOrder());
        return banners.save(banner);
    }

    @Override
    @Transactional
    public void deleteBanner(Integer id) {
        banners.delete(getBanner(id));
    }
}
