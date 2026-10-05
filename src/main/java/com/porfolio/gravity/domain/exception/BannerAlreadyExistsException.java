package com.porfolio.gravity.domain.exception;

public class BannerAlreadyExistsException extends RuntimeException {
    public BannerAlreadyExistsException(Integer profileId) {
        super("Profile with ID " + profileId + " already has a banner");
    }
}
