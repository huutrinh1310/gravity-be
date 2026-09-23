package com.porfolio.gravity.adapter.in.web.profile;

import com.porfolio.gravity.application.port.in.profile.CreateProjectCommand;
import com.porfolio.gravity.application.port.in.profile.ProjectUseCase;
import com.porfolio.gravity.application.port.in.profile.UpdateProjectCommand;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@Tag(name = "Projects", description = "Project management")
@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectUseCase useCase;

    public ProjectController(ProjectUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    public List<ProjectResponse> list() {
        return useCase.listProjects().stream().map(ProjectResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ProjectResponse get(@PathVariable Integer id) {
        return ProjectResponse.from(useCase.getProject(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(@Valid @RequestBody ProjectRequest request) {
        return ProjectResponse.from(useCase.createProject(new CreateProjectCommand(request.profileId(), request.name(), request.description(), request.skills())));
    }

    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable Integer id, @Valid @RequestBody UpdateProjectRequest request) {
        return ProjectResponse.from(useCase.updateProject(id, new UpdateProjectCommand(request.name(), request.description(), request.skills())));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        useCase.deleteProject(id);
    }
}
