package com.porfolio.gravity.domain.model;

import com.porfolio.gravity.domain.exception.DomainValidationException;

public final class Banner {
    private Integer id;
    private String title;
    private String subtitle;
    private String imageUrl;
    private String linkUrl;
    private boolean active;
    private Integer sortOrder;

    public Banner(Integer id, String title, String subtitle, String imageUrl, String linkUrl, Boolean active, Integer sortOrder) {
        this.id = id;
        changeDetails(title, subtitle, imageUrl, linkUrl, active, sortOrder);
    }

    public static Banner create(String title, String subtitle, String imageUrl, String linkUrl, Boolean active, Integer sortOrder) {
        return new Banner(null, title, subtitle, imageUrl, linkUrl, active, sortOrder);
    }

    public void changeDetails(String title, String subtitle, String imageUrl, String linkUrl, Boolean active, Integer sortOrder) {
        this.title = required(title, "Banner title");
        this.subtitle = blankToNull(subtitle);
        this.imageUrl = required(imageUrl, "Banner image URL");
        this.linkUrl = blankToNull(linkUrl);
        this.active = active == null || active;
        this.sortOrder = sortOrder == null ? 0 : sortOrder;

        if (this.sortOrder < 0) {
            throw new DomainValidationException("Banner sort order cannot be negative");
        }
    }

    public Integer id() {
        return id;
    }

    public void assignId(Integer id) {
        this.id = id;
    }

    public String title() {
        return title;
    }

    public String subtitle() {
        return subtitle;
    }

    public String imageUrl() {
        return imageUrl;
    }

    public String linkUrl() {
        return linkUrl;
    }

    public boolean active() {
        return active;
    }

    public Integer sortOrder() {
        return sortOrder;
    }

    private static String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException(field + " is required");
        }
        return value.trim();
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
