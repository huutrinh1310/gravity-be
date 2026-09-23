package com.porfolio.gravity;

import com.porfolio.gravity.domain.exception.DomainValidationException;
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

        assertEquals("Java", profile.skills().get(0).name());
        assertEquals("java", profile.projects().get(0).skills().get(0).name());
        assertThrows(DomainValidationException.class, () -> profile.changeDetails("", "ada@example.com", "London", null));
        assertThrows(DomainValidationException.class, () -> profile.changeDetails("Ada", "not-an-email", "London", null));
    }
}
