package com.porfolio.gravity.adapter.in.web.profile;

import com.porfolio.gravity.application.port.in.profile.CreatePortfolioCommand;
import com.porfolio.gravity.application.port.in.profile.PortfolioUseCase;
import com.porfolio.gravity.application.port.in.profile.UpdatePortfolioCommand;
import com.porfolio.gravity.domain.model.PortfolioTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Portfolios", description = "Portfolio management")
@RestController
@RequestMapping("/portfolios")
public class PortfolioController {
    private final PortfolioUseCase useCase;

    public PortfolioController(PortfolioUseCase useCase) {
        this.useCase = useCase;
    }

    @Operation(summary = "List portfolios")
    @GetMapping
    public List<PortfolioResponse> list() {
        return useCase.listPortfolios().stream().map(PortfolioResponse::from).toList();
    }

    @Operation(summary = "Get portfolio by ID")
    @GetMapping("/{id}")
    public PortfolioResponse get(@PathVariable Integer id) {
        return PortfolioResponse.from(useCase.getPortfolio(id));
    }

    @Operation(summary = "Get portfolio by profile ID")
    @GetMapping("/profile/{id}")
    public List<PortfolioResponse> getByProfileId(@PathVariable Integer id) {
        return useCase.getPortfolioByProfile(id).stream().map(PortfolioResponse::from).toList();
    }

    @Operation(summary = "Create a portfolio")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PortfolioResponse create(@Valid @RequestBody PortfolioRequest request) {
        return PortfolioResponse.from(useCase.createPortfolio(new CreatePortfolioCommand(request.profileId(),
                request.name(), request.domainUrl(), templateFrom(request), request.description(), request.imageUrl(),
                request.isPublic(), request.isIntegrateAnalytics())));
    }

    @Operation(summary = "Update a portfolio")
    @PutMapping("/{id}")
    public PortfolioResponse update(@PathVariable Integer id, @Valid @RequestBody UpdatePortfolioRequest request) {
        return PortfolioResponse.from(useCase.updatePortfolio(id, new UpdatePortfolioCommand(request.name(),
                request.domainUrl(), request.template() == null ? null : request.template().toDomain(),
                request.description(), request.imageUrl(), request.isPublic(), request.isIntegrateAnalytics())));
    }

    @Operation(summary = "Delete a portfolio")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        useCase.deletePortfolio(id);
    }

    private PortfolioTemplate templateFrom(PortfolioRequest request) {
        return request.template() == null ? null : request.template().toDomain();
    }
}
