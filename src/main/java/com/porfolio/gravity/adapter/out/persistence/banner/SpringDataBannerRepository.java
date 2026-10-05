package com.porfolio.gravity.adapter.out.persistence.banner;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataBannerRepository extends JpaRepository<BannerJpaEntity, Integer> {
    Optional<BannerJpaEntity> findByProfile_Id(Integer profileId);
}
