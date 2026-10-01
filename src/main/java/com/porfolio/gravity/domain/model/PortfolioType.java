package com.porfolio.gravity.domain.model;

public final class PortfolioType {
    private final String name;

    public PortfolioType(String name) {
        this.name = name == null || name.isBlank() ? null : name.trim();
    }

    public String getName() {
        return name;
    }

    public String name() {
        return name;
    }
}
