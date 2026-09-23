package com.porfolio.gravity.adapter.out.persistence.profile;

import com.porfolio.gravity.application.port.out.profile.ProfileRepository;
import com.porfolio.gravity.domain.model.Profile;
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
                        project.skills.stream().map(skill -> new Skill(skill.id, skill.name)).toList(), null, null)).toList());
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
        return target;
    }
}
