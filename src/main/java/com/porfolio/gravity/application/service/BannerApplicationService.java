package com.porfolio.gravity.application.service;

import com.porfolio.gravity.application.port.in.banner.BannerUseCase;
import com.porfolio.gravity.application.port.in.banner.CreateBannerCommand;
import com.porfolio.gravity.application.port.in.banner.UpdateBannerCommand;
import com.porfolio.gravity.application.port.out.banner.BannerRepository;
import com.porfolio.gravity.application.port.out.profile.ProfileRepository;
import com.porfolio.gravity.domain.exception.BannerAlreadyExistsException;
import com.porfolio.gravity.domain.exception.BannerResourceNotFoundException;
import com.porfolio.gravity.domain.exception.DomainValidationException;
import com.porfolio.gravity.domain.exception.ProfileResourceNotFoundException;
import com.porfolio.gravity.domain.model.Banner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BannerApplicationService implements BannerUseCase {
    private final BannerRepository banners;
    private final ProfileRepository profiles;

    public BannerApplicationService(BannerRepository banners, ProfileRepository profiles) {
        this.banners = banners;
        this.profiles = profiles;
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
        requireProfile(command.profileId());
        if (banners.findByProfileId(command.profileId()).isPresent()) {
            throw new BannerAlreadyExistsException(command.profileId());
        }
        return banners.save(Banner.create(command.profileId(), command.title(), command.subtitle(), command.imageUrl(),
                command.linkUrl(), command.active(), command.sortOrder()));
    }

    @Override
    @Transactional
    public Banner updateBanner(Integer id, UpdateBannerCommand command) {
        Banner banner = getBanner(id);
        if (!banner.profileId().equals(command.profileId())) {
            requireProfile(command.profileId());
            banners.findByProfileId(command.profileId())
                    .filter(existing -> !existing.id().equals(id))
                    .ifPresent(existing -> {
                        throw new BannerAlreadyExistsException(command.profileId());
                    });
            banner.changeProfile(command.profileId());
        }
        banner.changeDetails(command.title(), command.subtitle(), command.imageUrl(), command.linkUrl(), command.active(), command.sortOrder());
        return banners.save(banner);
    }

    @Override
    @Transactional
    public void deleteBanner(Integer id) {
        banners.delete(getBanner(id));
    }

    @Override
    public Banner getBannerByProfile(Integer profileId) {
        requireProfile(profileId);

        return banners.findByProfileId(profileId).orElseThrow(() -> new BannerResourceNotFoundException("Banner with profile ID " + profileId + " not found"));
    }

    private void requireProfile(Integer profileId) {
        if (profileId == null) {
            throw new DomainValidationException("Banner profile ID is required");
        }
        if (!profiles.existsById(profileId)) {
            throw new ProfileResourceNotFoundException("Profile with ID " + profileId + " not found");
        }
    }
}
