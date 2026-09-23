package com.porfolio.gravity.adapter.out.persistence.banner;

import com.porfolio.gravity.application.port.out.banner.BannerRepository;
import com.porfolio.gravity.domain.model.Banner;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BannerPersistenceAdapter implements BannerRepository {
    private final SpringDataBannerRepository repository;

    public BannerPersistenceAdapter(SpringDataBannerRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Banner> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Banner> findById(Integer id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Banner save(Banner banner) {
        return toDomain(repository.save(toEntity(banner)));
    }

    @Override
    public void delete(Banner banner) {
        repository.deleteById(banner.id());
    }

    private Banner toDomain(BannerJpaEntity source) {
        return new Banner(source.id, source.title, source.subtitle, source.imageUrl, source.linkUrl, source.active, source.sortOrder);
    }

    private BannerJpaEntity toEntity(Banner source) {
        BannerJpaEntity target = new BannerJpaEntity();
        target.id = source.id();
        target.title = source.title();
        target.subtitle = source.subtitle();
        target.imageUrl = source.imageUrl();
        target.linkUrl = source.linkUrl();
        target.active = source.active();
        target.sortOrder = source.sortOrder();
        return target;
    }
}
