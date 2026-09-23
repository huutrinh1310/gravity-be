package com.porfolio.gravity.application.port.in.profile;

import com.porfolio.gravity.domain.model.Profile;

import java.util.List;

/**
 * Inbound port for profile lifecycle operations.
 */
public interface ProfileUseCase {
    List<Profile> listProfiles();

    Profile getProfile(Integer id);

    Profile createProfile(CreateProfileCommand command);

    Profile updateProfile(Integer id, UpdateProfileCommand command);

    void deleteProfile(Integer id);
}
