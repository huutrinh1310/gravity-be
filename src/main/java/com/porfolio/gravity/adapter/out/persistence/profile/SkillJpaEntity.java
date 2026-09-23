package com.porfolio.gravity.adapter.out.persistence.profile;

import jakarta.persistence.*;

@Entity
@Table(name = "skills")
public class SkillJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_id")
    Integer id;
    @Column(nullable = false)
    String name;
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "profile_id", nullable = true)
    ProfileJpaEntity profile;
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "project_id", nullable = true)
    ProjectJpaEntity project;
}
