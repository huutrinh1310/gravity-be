package com.porfolio.gravity.application.port.out.banner;

import com.porfolio.gravity.domain.model.Banner;

import java.util.List;
import java.util.Optional;

public interface BannerRepository {
    List<Banner> findAll();

    Optional<Banner> findById(Integer id);

    Banner save(Banner banner);

    void delete(Banner banner);
}
