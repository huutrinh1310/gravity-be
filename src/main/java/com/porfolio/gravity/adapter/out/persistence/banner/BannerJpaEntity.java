package com.porfolio.gravity.adapter.out.persistence.banner;

import com.porfolio.gravity.adapter.out.persistence.common.BaseEntity;
import com.porfolio.gravity.adapter.out.persistence.profile.ProfileJpaEntity;
import com.porfolio.gravity.domain.model.Banner;
import jakarta.persistence.*;

@Entity
@Table(name = "banners")
public class BannerJpaEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "banner_id")
    Integer id;

    @Column(nullable = false)
    String title;

    String subtitle;

    @Column(nullable = false, length = 2000)
    String imageUrl;

    @Column(length = 2000)
    String linkUrl;

    @Column(nullable = false)
    boolean active = true;

    @Column(nullable = false)
    Integer sortOrder;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = true, unique = true)
    ProfileJpaEntity profile;

    public Banner toDomain() {
        return new Banner(id, profile.getId(), title, subtitle, imageUrl, linkUrl, active, sortOrder);
    }
}
