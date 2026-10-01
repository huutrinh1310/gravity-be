package com.porfolio.gravity.adapter.out.persistence.profile;

import com.porfolio.gravity.adapter.out.persistence.common.BaseEntity;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "projects")
public class ProjectJpaEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    Integer id;

    @Column(nullable = false)
    String name;
    String description;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<SkillJpaEntity> skills = new java.util.LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    ProfileJpaEntity profile;
}
