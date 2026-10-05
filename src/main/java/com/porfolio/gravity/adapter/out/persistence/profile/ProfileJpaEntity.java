package com.porfolio.gravity.adapter.out.persistence.profile;

import com.porfolio.gravity.adapter.out.persistence.banner.BannerJpaEntity;
import jakarta.persistence.*;
import com.porfolio.gravity.adapter.out.persistence.common.BaseEntity;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "profiles")
@ToString
public class ProfileJpaEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    Integer id;
    @Column(nullable = false)
    String name;
    @Column(nullable = false)
    String email;
    @Column(nullable = false)
    String address;
    String phone;
    @Column(name = "password_hash")
    String passwordHash;
    @Column(name = "auth_provider", length = 20)
    String authProvider;
    @Column(name = "provider_id", length = 255)
    String providerId;
    @Column(length = 30)
    String role;
    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    BannerJpaEntity banner;
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<SkillJpaEntity> skills = new LinkedHashSet<>();
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<ProjectJpaEntity> projects = new LinkedHashSet<>();
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<PortfolioJpaEntity> portfolios = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }
}
