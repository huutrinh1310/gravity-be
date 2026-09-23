package com.porfolio.gravity.domain.model;

import com.porfolio.gravity.domain.exception.DomainValidationException;

public final class Skill {
    private Integer id;
    private String name;

    public Skill(Integer id, String name) { this.id = id; rename(name); }
    public static Skill create(String name) { return new Skill(null, name); }
    public void rename(String name) {
        if (name == null || name.isBlank()) throw new DomainValidationException("Skill name is required");
        this.name = name.trim();
    }
    public Integer id() { return id; }
    public void assignId(Integer id) { this.id = id; }
    public String name() { return name; }
}
