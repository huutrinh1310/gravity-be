package com.porfolio.gravity.adapter.in.web.profile;

import com.porfolio.gravity.application.port.in.profile.CreateProfileCommand;
import com.porfolio.gravity.application.port.in.profile.ProfileUseCase;
import com.porfolio.gravity.application.port.in.profile.UpdateProfileCommand;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@Tag(name = "Profiles", description = "Profile management")
@RestController
@RequestMapping("/profiles")
public class ProfileController {
    private final ProfileUseCase useCase;

    public ProfileController(ProfileUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    public List<ProfileResponse> list() {
        return useCase.listProfiles().stream().map(ProfileResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ProfileResponse get(@PathVariable Integer id) {
        return ProfileResponse.from(useCase.getProfile(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse create(@Valid @RequestBody ProfileRequest request) {
        return ProfileResponse.from(useCase.createProfile(new CreateProfileCommand(request.name(), request.email(), request.address(), request.phone())));
    }

    @PutMapping("/{id}")
    public ProfileResponse update(@PathVariable Integer id, @Valid @RequestBody ProfileRequest request) {
        return ProfileResponse.from(useCase.updateProfile(id, new UpdateProfileCommand(request.name(), request.email(), request.address(), request.phone())));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        useCase.deleteProfile(id);
    }
}
