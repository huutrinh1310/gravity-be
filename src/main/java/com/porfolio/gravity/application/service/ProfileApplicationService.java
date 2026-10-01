package com.porfolio.gravity.application.service;

import com.porfolio.gravity.application.port.out.profile.ProfileRepository;
import com.porfolio.gravity.application.port.in.profile.*;
import com.porfolio.gravity.domain.model.Portfolio;
import com.porfolio.gravity.domain.model.Profile;
import com.porfolio.gravity.domain.exception.ProfileResourceNotFoundException;
import com.porfolio.gravity.domain.model.Project;
import com.porfolio.gravity.domain.model.Skill;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ProfileApplicationService implements ProfileUseCase, SkillUseCase, ProjectUseCase, PortfolioUseCase {
    private final ProfileRepository profiles;

    public ProfileApplicationService(ProfileRepository profiles) {
        this.profiles = profiles;
    }

    public List<Profile> listProfiles() {
        return profiles.findAll();
    }

    public Profile getProfile(Integer id) {
        return load(id);
    }

    @Transactional
    public Profile createProfile(CreateProfileCommand command) {
        return profiles.save(Profile.create(command.name(), command.email(), command.address(), command.phone()));
    }

    @Transactional
    public Profile updateProfile(Integer id, UpdateProfileCommand command) {
        Profile profile = load(id);
        profile.changeDetails(command.name(), command.email(), command.address(), command.phone());
        return profiles.save(profile);
    }

    @Transactional
    public void deleteProfile(Integer id) {
        profiles.delete(load(id));
    }

    public List<Skill> listSkills() {
        return profiles.findAll().stream().flatMap(profile -> profile.skills().stream()).toList();
    }

    public Skill getSkill(Integer id) {
        return profileContainingSkill(id).findSkill(id);
    }

    @Transactional
    public Skill createSkill(CreateSkillCommand command) {
        Profile profile = load(command.profileId());
        Set<Integer> existingIds = profile.skills().stream().map(Skill::id).filter(java.util.Objects::nonNull).collect(java.util.stream.Collectors.toSet());
        profile.addSkill(command.name());
        return profiles.save(profile).skills().stream()
                .filter(skill -> skill.id() != null && !existingIds.contains(skill.id()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Saved skill was not assigned an ID"));
    }

    @Transactional
    public Skill updateSkill(Integer id, UpdateSkillCommand command) {
        Profile profile = profileContainingSkill(id);
        profile.changeSkill(id, command.name());
        profiles.save(profile);
        return profile.findSkill(id);
    }

    @Transactional
    public void deleteSkill(Integer id) {
        Profile profile = profileContainingSkill(id);
        profile.removeSkill(id);
        profiles.save(profile);
    }

    public List<Project> listProjects() {
        return profiles.findAll().stream().flatMap(profile -> profile.projects().stream()).toList();
    }

    public Project getProject(Integer id) {
        return profileContainingProject(id).findProject(id);
    }

    @Transactional
    public Project createProject(CreateProjectCommand command) {
        Profile profile = load(command.profileId());
        Set<Integer> existingIds = profile.projects().stream().map(Project::id).filter(java.util.Objects::nonNull).collect(java.util.stream.Collectors.toSet());
        profile.addProject(command.name(), command.description(), command.skills());
        return profiles.save(profile).projects().stream()
                .filter(project -> project.id() != null && !existingIds.contains(project.id()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Saved project was not assigned an ID"));
    }

    @Transactional
    public Project updateProject(Integer id, UpdateProjectCommand command) {
        Profile profile = profileContainingProject(id);
        profile.changeProject(id, command.name(), command.description(), command.skills());
        profiles.save(profile);
        return profile.findProject(id);
    }

    @Transactional
    public void deleteProject(Integer id) {
        Profile profile = profileContainingProject(id);
        profile.removeProject(id);
        profiles.save(profile);
    }

    @Override
    public List<Portfolio> listPortfolios() {
        return profiles.findAll().stream().flatMap(profile -> profile.portfolios().stream()).toList();
    }

    @Override
    public Portfolio getPortfolio(Integer id) {
        return profileContainingPortfolio(id).findPortfolio(id);
    }

    @Transactional
    @Override
    public Portfolio createPortfolio(CreatePortfolioCommand command) {
        Profile profile = load(command.profileId());
        Set<Integer> existingIds = profile.portfolios().stream()
                .map(Portfolio::id).filter(java.util.Objects::nonNull).collect(java.util.stream.Collectors.toSet());
        profile.addPortfolio(command.name(), command.domainUrl(), command.template(), command.description(),
                command.imageUrl(), command.isPublic(), command.isIntegrateAnalytics());
        return profiles.save(profile).portfolios().stream()
                .filter(portfolio -> portfolio.id() != null && !existingIds.contains(portfolio.id()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Saved portfolio was not assigned an ID"));
    }

    @Transactional
    @Override
    public Portfolio updatePortfolio(Integer id, UpdatePortfolioCommand command) {
        Profile profile = profileContainingPortfolio(id);
        profile.changePortfolio(id, command.name(), command.domainUrl(), command.template(), command.description(),
                command.imageUrl(), command.isPublic(), command.isIntegrateAnalytics());
        profiles.save(profile);
        return profile.findPortfolio(id);
    }

    @Transactional
    @Override
    public void deletePortfolio(Integer id) {
        Profile profile = profileContainingPortfolio(id);
        profile.removePortfolio(id);
        profiles.save(profile);
    }

    private Profile load(Integer id) {
        return profiles.findById(id).orElseThrow(() -> notFound("Profile", id));
    }

    private Profile profileContainingSkill(Integer id) {
        return profiles.findBySkillId(id).orElseThrow(() -> notFound("Skill", id));
    }

    private Profile profileContainingProject(Integer id) {
        return profiles.findByProjectId(id).orElseThrow(() -> notFound("Project", id));
    }

    private Profile profileContainingPortfolio(Integer id) {
        return profiles.findByPortfolioId(id).orElseThrow(() -> notFound("Portfolio", id));
    }

    private ProfileResourceNotFoundException notFound(String type, Integer id) {
        return new ProfileResourceNotFoundException(type + " with ID " + id + " not found");
    }
}
