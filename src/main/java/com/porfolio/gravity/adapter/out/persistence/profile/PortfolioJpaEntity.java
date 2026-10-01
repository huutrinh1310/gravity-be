package com.porfolio.gravity.adapter.out.persistence.profile;

import com.porfolio.gravity.adapter.out.persistence.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "portfolios")
public class PortfolioJpaEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_id")
    Integer id;

    @Column(nullable = false)
    String name;
    String domainUrl;
    String description;
    String imageUrl;
    @Column(nullable = false)
    Boolean isPublic = false;
    @Column(nullable = false)
    Boolean isIntegrateAnalytics = false;

    Integer templateId;
    String templateName;
    String templateDescription;
    String templateImageUrl;
    String templateTypeName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    ProfileJpaEntity profile;
}
