package com.porfolio.gravity;

import com.porfolio.gravity.domain.exception.DomainValidationException;
import com.porfolio.gravity.domain.model.Portfolio;
import com.porfolio.gravity.domain.model.Profile;
import com.porfolio.gravity.domain.model.Skill;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GravityApplicationTests {
    @Test
    void profileOwnsAndValidatesItsPortfolioItems() {
        Profile profile = Profile.create("Ada", "ada@example.com", "London", null);
        profile.addSkill("Java");
        profile.addProject("Gravity", "Portfolio API", List.of(Skill.create("java")));
        Portfolio portfolio = profile.addPortfolio("Gravity", "gravity.example", null, "Portfolio site",
                "https://example.com/image.png", true, false);

        assertEquals("Java", profile.skills().get(0).name());
        assertEquals("java", profile.projects().get(0).skills().get(0).name());
        assertEquals("Gravity", profile.portfolios().get(0).name());
        assertTrue(portfolio.isPublic());
        profile.changePortfolio(portfolio.id(), "Gravity updated", null, null, null, null, false, null);
        assertEquals("Gravity updated", profile.findPortfolio(portfolio.id()).name());
        profile.removePortfolio(portfolio.id());
        assertTrue(profile.portfolios().isEmpty());
        assertThrows(DomainValidationException.class, () -> profile.changeDetails("", "ada@example.com", "London", null));
        assertThrows(DomainValidationException.class, () -> profile.changeDetails("Ada", "not-an-email", "London", null));
        assertThrows(DomainValidationException.class,
                () -> profile.addPortfolio(" ", null, null, null, null, null, null));
    }
}
