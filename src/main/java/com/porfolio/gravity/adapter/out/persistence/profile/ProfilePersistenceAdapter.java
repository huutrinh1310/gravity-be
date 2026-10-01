package com.porfolio.gravity.adapter.out.persistence.profile;

import com.porfolio.gravity.application.port.out.profile.ProfileRepository;
import com.porfolio.gravity.domain.model.Profile;
import com.porfolio.gravity.domain.model.Portfolio;
import com.porfolio.gravity.domain.model.PortfolioTemplate;
import com.porfolio.gravity.domain.model.PortfolioType;
import com.porfolio.gravity.domain.model.Project;
import com.porfolio.gravity.domain.model.Skill;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProfilePersistenceAdapter implements ProfileRepository {
    private final SpringDataProfileRepository repository;

    public ProfilePersistenceAdapter(SpringDataProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Profile> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Profile> findById(Integer id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Profile> findBySkillId(Integer skillId) {
        return repository.findBySkillId(skillId).map(this::toDomain);
    }

    @Override
    public Optional<Profile> findByProjectId(Integer projectId) {
        return repository.findByProjectId(projectId).map(this::toDomain);
    }

    @Override
    public Optional<Profile> findByPortfolioId(Integer portfolioId) {
        return repository.findByPortfolioId(portfolioId).map(this::toDomain);
    }

    @Override
    public Profile save(Profile profile) {
        return toDomain(repository.save(toEntity(profile)));
    }

    @Override
    public void delete(Profile profile) {
        repository.deleteById(profile.id());
    }

    private Profile toDomain(ProfileJpaEntity source) {
        return new Profile(source.id, source.name, source.email, source.address, source.phone,
                source.skills.stream().map(skill -> new Skill(skill.id, skill.name)).toList(),
                source.projects.stream().map(project -> new Project(project.id, project.name, project.description,
                        project.skills.stream().map(skill -> new Skill(skill.id, skill.name)).toList(), null, null)).toList(),
                source.portfolios.stream().map(portfolio -> new Portfolio(portfolio.id, portfolio.name, portfolio.domainUrl,
                        templateFrom(portfolio), portfolio.description, portfolio.imageUrl, portfolio.isPublic,
                        portfolio.isIntegrateAnalytics)).toList());
    }

    private ProfileJpaEntity toEntity(Profile source) {
        ProfileJpaEntity target = new ProfileJpaEntity();
        target.id = source.id();
        target.name = source.name();
        target.email = source.email();
        target.address = source.address();
        target.phone = source.phone();
        for (Skill skill : source.skills()) {
            SkillJpaEntity child = new SkillJpaEntity();
            child.id = skill.id();
            child.name = skill.name();
            child.profile = target;
            target.skills.add(child);
        }
        for (Project project : source.projects()) {
            ProjectJpaEntity child = new ProjectJpaEntity();
            child.id = project.id();
            child.name = project.name();
            child.description = project.description();
            child.profile = target;
            target.projects.add(child);
            for (Skill skill : project.skills()) {
                SkillJpaEntity skillChild = new SkillJpaEntity();
                skillChild.id = skill.id();
                skillChild.name = skill.name();
                skillChild.project = child;
                skillChild.profile = target; // associate with profile as well
                child.skills.add(skillChild);
            }
        }
        for (Portfolio portfolio : source.portfolios()) {
            PortfolioJpaEntity child = new PortfolioJpaEntity();
            child.id = portfolio.id();
            child.name = portfolio.name();
            child.domainUrl = portfolio.domainUrl();
            child.description = portfolio.description();
            child.imageUrl = portfolio.imageUrl();
            child.isPublic = portfolio.isPublic();
            child.isIntegrateAnalytics = portfolio.isIntegrateAnalytics();
            if (portfolio.template() != null) {
                child.templateId = portfolio.template().id();
                child.templateName = portfolio.template().name();
                child.templateDescription = portfolio.template().description();
                child.templateImageUrl = portfolio.template().imageUrl();
                child.templateTypeName = portfolio.template().type() == null ? null : portfolio.template().type().name();
            }
            child.profile = target;
            target.portfolios.add(child);
        }
        return target;
    }

    private PortfolioTemplate templateFrom(PortfolioJpaEntity source) {
        if (source.templateId == null && source.templateName == null && source.templateDescription == null
                && source.templateImageUrl == null && source.templateTypeName == null) {
            return null;
        }
        PortfolioType type = source.templateTypeName == null ? null : new PortfolioType(source.templateTypeName);
        return new PortfolioTemplate(source.templateId, source.templateName, source.templateDescription,
                source.templateImageUrl, type);
    }
}
