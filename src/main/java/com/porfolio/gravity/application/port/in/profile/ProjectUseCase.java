package com.porfolio.gravity.application.port.in.profile;

import com.porfolio.gravity.domain.model.Project;

import java.util.List;

/**
 * Inbound port for projects owned by a profile.
 */
public interface ProjectUseCase {
    List<Project> listProjects();

    Project getProject(Integer id);

    Project createProject(CreateProjectCommand command);

    Project updateProject(Integer id, UpdateProjectCommand command);

    void deleteProject(Integer id);
}
