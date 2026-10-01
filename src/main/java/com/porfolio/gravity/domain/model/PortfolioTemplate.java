package com.porfolio.gravity.domain.model;

public final class PortfolioTemplate {
    private Integer id;
    private String name;
    private String description;
    private String imageUrl;
    private PortfolioType type;

    public PortfolioTemplate(Integer id, String name, String description, String imageUrl, PortfolioType type) {
        this.id = id;
        this.name = blankToNull(name);
        this.description = blankToNull(description);
        this.imageUrl = blankToNull(imageUrl);
        this.type = type;
    }

    public Integer id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public String imageUrl() {
        return imageUrl;
    }

    public PortfolioType type() {
        return type;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
