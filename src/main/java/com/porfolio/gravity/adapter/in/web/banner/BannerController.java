package com.porfolio.gravity.adapter.in.web.banner;

import com.porfolio.gravity.application.port.in.banner.BannerUseCase;
import com.porfolio.gravity.application.port.in.banner.CreateBannerCommand;
import com.porfolio.gravity.application.port.in.banner.UpdateBannerCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Banners", description = "Banner catalog management")
@RestController
@RequestMapping("/banners")
public class BannerController {
    private final BannerUseCase useCase;

    public BannerController(BannerUseCase useCase) {
        this.useCase = useCase;
    }

    @Operation(summary = "List banners")
    @GetMapping
    public List<BannerResponse> list() {
        return useCase.listBanners().stream().map(BannerResponse::from).toList();
    }

    @Operation(summary = "Get banner by ID")
    @GetMapping("/{id}")
    public BannerResponse get(@PathVariable Integer id) {
        return BannerResponse.from(useCase.getBanner(id));
    }

    @Operation(summary = "Create a banner")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BannerResponse create(@Valid @RequestBody BannerRequest request) {
        return BannerResponse.from(useCase.createBanner(new CreateBannerCommand(
                request.title(),
                request.subtitle(),
                request.imageUrl(),
                request.linkUrl(),
                request.active(),
                request.sortOrder()
        )));
    }

    @Operation(summary = "Update a banner")
    @PutMapping("/{id}")
    public BannerResponse update(@PathVariable Integer id, @Valid @RequestBody BannerRequest request) {
        return BannerResponse.from(useCase.updateBanner(id, new UpdateBannerCommand(
                request.title(),
                request.subtitle(),
                request.imageUrl(),
                request.linkUrl(),
                request.active(),
                request.sortOrder()
        )));
    }

    @Operation(summary = "Delete a banner")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        useCase.deleteBanner(id);
    }
}
