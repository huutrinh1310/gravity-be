package com.porfolio.gravity.domain.model;

import com.porfolio.gravity.domain.exception.DomainValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public final class Project {
    private Integer id;
    private String name;
    private String description;
    private List<Skill> skills;
    private LocalDateTime startYearExperience;
    private LocalDateTime endYearExperience;


    public Project(Integer id, String name, String description, List<Skill> skills, LocalDateTime startYearExperience, LocalDateTime endYearExperience) {
        this.id = id;
        changeDetails(name, description, skills);
        this.startYearExperience = startYearExperience;
        this.endYearExperience = endYearExperience;
    }

    public static Project create(String name, String description, List<Skill> skills, LocalDateTime startYearExperience, LocalDateTime endYearExperience) {
        return new Project(null, name, description, skills, startYearExperience, endYearExperience);
    }

    public void changeDetails(String name, String description, List<Skill> skills) {
        if (name == null || name.isBlank()) throw new DomainValidationException("Project name is required");
        this.name = name.trim();
        this.description = description == null || description.isBlank() ? null : description.trim();
        this.skills = skills == null ? List.of() : skills.stream().filter(Objects::nonNull).distinct().toList();
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

    public String description() {
        return description;
    }

    public List<Skill> skills() {
        return skills;
    }
}
