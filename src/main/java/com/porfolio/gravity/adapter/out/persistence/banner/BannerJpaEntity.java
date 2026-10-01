package com.porfolio.gravity.adapter.out.persistence.banner;

import com.porfolio.gravity.adapter.out.persistence.common.BaseEntity;
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
}
