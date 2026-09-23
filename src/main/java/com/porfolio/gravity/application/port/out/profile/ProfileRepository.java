package com.porfolio.gravity.application.port.out.profile;

import com.porfolio.gravity.domain.model.Profile;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port: the application does not know about JPA or a database.
 */
public interface ProfileRepository {
    List<Profile> findAll();

    Optional<Profile> findById(Integer id);

    Optional<Profile> findBySkillId(Integer skillId);

    Optional<Profile> findByProjectId(Integer projectId);

    Profile save(Profile profile);

    void delete(Profile profile);
}
