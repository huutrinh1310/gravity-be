package com.porfolio.gravity.adapter.out.persistence.profile;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface SpringDataProfileRepository extends JpaRepository<ProfileJpaEntity, Integer> {
    @Override
    @EntityGraph(attributePaths = {"skills", "projects", "projects.skills", "portfolios"})
    List<ProfileJpaEntity> findAll();

    @Override
    @EntityGraph(attributePaths = {"skills", "projects", "projects.skills", "portfolios"})
    Optional<ProfileJpaEntity> findById(Integer id);

    @EntityGraph(attributePaths = {"skills", "projects", "projects.skills", "portfolios"})
    @Query("select distinct profile from ProfileJpaEntity profile join profile.skills skill where skill.id = :skillId")
    Optional<ProfileJpaEntity> findBySkillId(@Param("skillId") Integer skillId);

    @EntityGraph(attributePaths = {"skills", "projects", "projects.skills", "portfolios"})
    @Query("select distinct profile from ProfileJpaEntity profile join profile.projects project where project.id = :projectId")
    Optional<ProfileJpaEntity> findByProjectId(@Param("projectId") Integer projectId);

    @EntityGraph(attributePaths = {"skills", "projects", "projects.skills", "portfolios"})
    @Query("select distinct profile from ProfileJpaEntity profile join profile.portfolios portfolio where portfolio.id = :portfolioId")
    Optional<ProfileJpaEntity> findByPortfolioId(@Param("portfolioId") Integer portfolioId);
}
