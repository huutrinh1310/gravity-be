package com.porfolio.gravity.configs.security;

import com.porfolio.gravity.application.port.in.auth.AuthUseCase;
import com.porfolio.gravity.application.port.in.profile.ProfileUseCase;
import com.porfolio.gravity.domain.model.Profile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = com.porfolio.gravity.adapter.in.web.profile.ProfileController.class, properties = {
        "app.jwt.secret=01234567890123456789012345678901",
        "app.jwt.access-token-expiration-ms=900000",
        "app.jwt.refresh-token-expiration-ms=604800000",
        "app.oauth2.frontend-redirect-uri=http://localhost:3000/oauth/callback",
        "spring.security.oauth2.client.registration.google.client-id=test-google",
        "spring.security.oauth2.client.registration.google.client-secret=test-secret",
        "spring.security.oauth2.client.registration.github.client-id=test-github",
        "spring.security.oauth2.client.registration.github.client-secret=test-secret"
})
@Import({SecurityConfig.class, JwtTokenProvider.class, OAuth2SuccessHandler.class})
class SecurityConfigTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @MockitoBean
    private ProfileUseCase profileUseCase;

    @MockitoBean
    private AuthUseCase authUseCase;

    @MockitoBean
    private OAuth2AuthorizedClientService authorizedClientService;

    @Test
    void publicGetRemainsAccessible() throws Exception {
        when(profileUseCase.listProfiles()).thenReturn(List.of());

        mockMvc.perform(get("/profiles")).andExpect(status().isOk());
    }

    @Test
    void protectedMutationRejectsMissingJwt() throws Exception {
        mockMvc.perform(post("/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedMutationRejectsInvalidJwt() throws Exception {
        mockMvc.perform(post("/profiles")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedMutationAcceptsUserRole() throws Exception {
        when(profileUseCase.createProfile(any())).thenReturn(Profile.create("Ada", "ada@example.com", "London", null));
        String token = tokenProvider.generateAccessToken(7, "ada@example.com", "ROLE_USER");

        mockMvc.perform(post("/profiles")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ada\",\"email\":\"ada@example.com\",\"address\":\"London\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void protectedMutationRejectsInsufficientRole() throws Exception {
        String token = tokenProvider.generateAccessToken(7, "ada@example.com", "ROLE_GUEST");

        mockMvc.perform(post("/profiles")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ada\",\"email\":\"ada@example.com\",\"address\":\"London\"}"))
                .andExpect(status().isForbidden());
    }
}