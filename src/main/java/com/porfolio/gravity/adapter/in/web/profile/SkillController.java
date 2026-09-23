package com.porfolio.gravity.adapter.in.web.profile;

import com.porfolio.gravity.application.port.in.profile.CreateSkillCommand;
import com.porfolio.gravity.application.port.in.profile.SkillUseCase;
import com.porfolio.gravity.application.port.in.profile.UpdateSkillCommand;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@Tag(name = "Skills", description = "Skill management")
@RestController
@RequestMapping("/skills")
public class SkillController {
    private final SkillUseCase useCase;

    public SkillController(SkillUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    public List<SkillResponse> list() {
        return useCase.listSkills().stream().map(SkillResponse::from).toList();
    }

    @GetMapping("/{id}")
    public SkillResponse get(@PathVariable Integer id) {
        return SkillResponse.from(useCase.getSkill(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SkillResponse create(@Valid @RequestBody SkillRequest request) {
        return SkillResponse.from(useCase.createSkill(new CreateSkillCommand(request.profileId(), request.name())));
    }

    @PutMapping("/{id}")
    public SkillResponse update(@PathVariable Integer id, @Valid @RequestBody UpdateSkillRequest request) {
        return SkillResponse.from(useCase.updateSkill(id, new UpdateSkillCommand(request.name())));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        useCase.deleteSkill(id);
    }
}
