package com.porfolio.gravity.domain.model;

import com.porfolio.gravity.domain.exception.DomainValidationException;
import com.porfolio.gravity.domain.exception.ProfileResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * The aggregate root for everything shown on a portfolio profile.
 */
public final class Profile {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private Integer id;
    private String name;
    private String email;
    private String address;
    private String phone;
    private String passwordHash;
    private String authProvider;
    private String providerId;
    private String role;
    private final Banner banner;
    private final List<Skill> skills;
    private final List<Project> projects;
    private final List<Portfolio> portfolios;

    public Profile(Integer id, String name, String email, String address, String phone,
                   List<Skill> skills, List<Project> projects) {
        this(id, name, email, address, phone, skills, projects, List.of());
    }

    public Profile(Integer id, String name, String email, String address, String phone,
                   List<Skill> skills, List<Project> projects, List<Portfolio> portfolios) {
        this(id, name, email, address, phone, skills, projects, portfolios, null, null, null, null);
    }

    public Profile(Integer id, String name, String email, String address, String phone,
                   List<Skill> skills, List<Project> projects, List<Portfolio> portfolios,
                   String passwordHash, String authProvider, String providerId, String role) {
        this(id, name, email, address, phone, skills, projects, portfolios, passwordHash, authProvider, providerId, role, null);
    }

    public Profile(Integer id, String name, String email, String address, String phone,
                   List<Skill> skills, List<Project> projects, List<Portfolio> portfolios,
                   String passwordHash, String authProvider, String providerId, String role, Banner banner) {
        if (banner != null && (id == null || !id.equals(banner.profileId()))) {
            throw new DomainValidationException("Banner must belong to its profile");
        }
        this.id = id;
        this.banner = banner;
        this.skills = new ArrayList<>(skills == null ? List.of() : skills);
        this.projects = new ArrayList<>(projects == null ? List.of() : projects);
        this.portfolios = new ArrayList<>(portfolios == null ? List.of() : portfolios);
        this.passwordHash = passwordHash;
        this.authProvider = authProvider;
        this.providerId = providerId;
        this.role = role;
        changeDetails(name, email, address, phone);
    }

    public static Profile create(String name, String email, String address, String phone) {
        return new Profile(null, name, email, address, phone, List.of(), List.of());
    }

    public static Profile createAuthenticated(String name, String email, String address, String phone,
                                              String passwordHash, String authProvider, String providerId, String role) {
        return new Profile(null, name, email, address, phone, List.of(), List.of(), List.of(),
                passwordHash, authProvider, providerId, role);
    }

    public void changeDetails(String name, String email, String address, String phone) {
        this.name = required(name, "Profile name");
        this.email = validEmail(email);
        this.address = allowsMissingAddress() ? blankToNull(address) : required(address, "Profile address");
        this.phone = blankToNull(phone);
    }

    public void linkOAuthProvider(String provider, String providerId) {
        if (this.providerId != null && (!provider.equals(authProvider) || !providerId.equals(this.providerId))) {
            throw new DomainValidationException("Profile is already linked to another OAuth identity");
        }
        this.authProvider = required(provider, "Authentication provider");
        this.providerId = required(providerId, "Provider ID");
    }

    public Skill addSkill(String name) {
        Skill skill = Skill.create(name);
        skills.add(skill);
        return skill;
    }

    public void changeSkill(Integer skillId, String name) {
        findSkill(skillId).rename(name);
    }

    public void removeSkill(Integer skillId) {
        skills.remove(findSkill(skillId));
    }

    public Project addProject(String name, String description, List<Skill> skills) {
        Project project = Project.create(name, description, skills, null, null);
        projects.add(project);
        return project;
    }

    public void changeProject(Integer projectId, String name, String description, List<Skill> skills) {
        findProject(projectId).changeDetails(name, description, skills);
    }

    public void removeProject(Integer projectId) {
        projects.remove(findProject(projectId));
    }

    public Portfolio addPortfolio(String name, String domainUrl, PortfolioTemplate template, String description,
                                  String imageUrl, Boolean isPublic, Boolean isIntegrateAnalytics) {
        Portfolio portfolio = Portfolio.create(name, domainUrl, template, description, imageUrl, isPublic, isIntegrateAnalytics);
        portfolios.add(portfolio);
        return portfolio;
    }

    public void changePortfolio(Integer portfolioId, String name, String domainUrl, PortfolioTemplate template,
                                String description, String imageUrl, Boolean isPublic, Boolean isIntegrateAnalytics) {
        findPortfolio(portfolioId).changeDetails(name, domainUrl, template, description, imageUrl, isPublic, isIntegrateAnalytics);
    }

    public void removePortfolio(Integer portfolioId) {
        portfolios.remove(findPortfolio(portfolioId));
    }

    public Skill findSkill(Integer skillId) {
        return skills.stream().filter(skill -> Objects.equals(skill.id(), skillId)).findFirst()
                .orElseThrow(() -> new ProfileResourceNotFoundException("Skill with ID " + skillId + " not found"));
    }

    public Project findProject(Integer projectId) {
        return projects.stream().filter(project -> Objects.equals(project.id(), projectId)).findFirst()
                .orElseThrow(() -> new ProfileResourceNotFoundException("Project with ID " + projectId + " not found"));
    }

    public Portfolio findPortfolio(Integer portfolioId) {
        return portfolios.stream().filter(portfolio -> Objects.equals(portfolio.id(), portfolioId)).findFirst()
                .orElseThrow(() -> new ProfileResourceNotFoundException("Portfolio with ID " + portfolioId + " not found"));
    }

    public Integer id() {
        return id;
    }

    public void assignId(Integer id) {
        this.id = id;
    }

    public String name() {
        return name;
    }

    public String email() {
        return email;
    }

    public String address() {
        return address;
    }

    public String phone() {
        return phone;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public String authProvider() {
        return authProvider;
    }

    public String providerId() {
        return providerId;
    }

    public String role() {
        return role;
    }

    public Banner banner() {
        return banner;
    }

    public List<Skill> skills() {
        return List.copyOf(skills);
    }

    public List<Project> projects() {
        return List.copyOf(projects);
    }

    public List<Portfolio> portfolios() {
        return List.copyOf(portfolios);
    }

    private static String required(String value, String field) {
        if (value == null || value.isBlank()) throw new DomainValidationException(field + " is required");
        return value.trim();
    }

    private static String validEmail(String value) {
        String email = required(value, "Profile email");
        if (!EMAIL_PATTERN.matcher(email).matches()) throw new DomainValidationException("Profile email must be valid");
        return email;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private boolean allowsMissingAddress() {
        return "google".equals(authProvider) || "github".equals(authProvider);
    }
}
