package com.porfolio.gravity.domain.model;

import com.porfolio.gravity.domain.exception.DomainValidationException;

public final class Portfolio {
    private Integer id;
    private String name;
    private String domainUrl;
    private PortfolioTemplate template;
    private String description;
    private String imageUrl;
    private Boolean isPublic;
    private Boolean isIntegrateAnalytics;

    public Portfolio(Integer id, String name, String domainUrl, PortfolioTemplate template, String description,
                     String imageUrl, Boolean isPublic, Boolean isIntegrateAnalytics) {
        this.id = id;
        changeDetails(name, domainUrl, template, description, imageUrl, isPublic, isIntegrateAnalytics);
    }

    public static Portfolio create(String name, String domainUrl, PortfolioTemplate template, String description,
                                   String imageUrl, Boolean isPublic, Boolean isIntegrateAnalytics) {
        return new Portfolio(null, name, domainUrl, template, description, imageUrl, isPublic, isIntegrateAnalytics);
    }

    public void changeDetails(String name, String domainUrl, PortfolioTemplate template, String description,
                              String imageUrl, Boolean isPublic, Boolean isIntegrateAnalytics) {
        if (name == null || name.isBlank()) {
            throw new DomainValidationException("Portfolio name is required");
        }
        this.name = name.trim();
        this.domainUrl = blankToNull(domainUrl);
        this.template = template;
        this.description = blankToNull(description);
        this.imageUrl = blankToNull(imageUrl);
        this.isPublic = isPublic != null && isPublic;
        this.isIntegrateAnalytics = isIntegrateAnalytics != null && isIntegrateAnalytics;
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

    public String domainUrl() {
        return domainUrl;
    }

    public PortfolioTemplate template() {
        return template;
    }

    public String description() {
        return description;
    }

    public String imageUrl() {
        return imageUrl;
    }

    public Boolean isPublic() {
        return isPublic;
    }

    public Boolean isIntegrateAnalytics() {
        return isIntegrateAnalytics;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
