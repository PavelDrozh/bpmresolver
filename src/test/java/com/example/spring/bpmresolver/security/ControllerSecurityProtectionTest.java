package com.example.spring.bpmresolver.security;

import com.example.spring.bpmresolver.config.SecurityConfig;
import com.example.spring.bpmresolver.controllers.rest.AuthController;
import com.example.spring.bpmresolver.controllers.rest.BpmFinishedProcessController;
import com.example.spring.bpmresolver.controllers.rest.QbpmcockpitController;
import com.example.spring.bpmresolver.controllers.view.BpmFinishedProcessViewController;
import com.example.spring.bpmresolver.controllers.view.QbpmcockpitViewController;
import com.example.spring.bpmresolver.controllers.view.ResolverTokenController;
import com.example.spring.bpmresolver.dto.BpmInstanceDto;
import com.example.spring.bpmresolver.dto.QbpmcockpitInstancesRequest;
import com.example.spring.bpmresolver.dto.RestResponsePage;
import com.example.spring.bpmresolver.localization.LocalizationService;
import com.example.spring.bpmresolver.services.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        AuthController.class,
        QbpmcockpitController.class,
        BpmFinishedProcessController.class,
        QbpmcockpitViewController.class,
        BpmFinishedProcessViewController.class,
        ResolverTokenController.class
})
@Import(SecurityConfig.class)
class ControllerSecurityProtectionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private JwtEncoder jwtEncoder;

    @MockBean
    private CookieBearerTokenResolver cookieBearerTokenResolver;

    @MockBean
    private UsersService usersService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenService jwtTokenService;

    @MockBean
    private AuthService authService;

    @MockBean
    private com.example.spring.bpmresolver.repositories.RevokedJwtRepository revokedJwtRepository;

    @MockBean
    private QbpmcockpitService qbpmcockpitService;

    @MockBean
    private BpmFinishedProcessService bpmFinishedProcessService;

    @MockBean
    private QbpmPlayerServiceNameService qbpmPlayerServiceNameService;

    @MockBean
    private QbpmPlayerService qbpmPlayerService;

    @MockBean
    private ResolverTokenService resolverTokenService;

    @MockBean
    private ResolverTokenUseCase resolverTokenUseCase;

    @MockBean
    private AccessTokenService accessTokenService;

    @MockBean
    private ResolverAccessTokenService resolverAccessTokenService;

    @MockBean
    private LocalizationService localizationService;

    @TestConfiguration
    static class TestKeysConfig {
        @Bean
        RSAPublicKey jwtPublicKey() throws Exception {
            return (RSAPublicKey) keyPair().getPublic();
        }

        @Bean
        RSAPrivateKey jwtPrivateKey() throws Exception {
            return (RSAPrivateKey) keyPair().getPrivate();
        }

        private static KeyPair keyPair() throws Exception {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            return kpg.generateKeyPair();
        }
    }

    @Test
    void apiEndpoints_requireAuthentication_return401WhenAnonymous() throws Exception {
        mockMvc.perform(get("/api/qbpmcockpit/instances").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/api/finished-processes").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void apiEndpoints_allowJwtAuthentication() throws Exception {
        when(qbpmcockpitService.getInstances(any(QbpmcockpitInstancesRequest.class)))
                .thenReturn(new RestResponsePage<>(List.of(BpmInstanceDto.builder().id("1").build())));

        mockMvc.perform(get("/api/qbpmcockpit/instances")
                        .with(jwt().jwt(j -> j.subject("john").claim("roles", List.of("ROLE_USER")))))
                .andExpect(status().isOk());
    }

    @Test
    void viewEndpoints_requireAuthentication_redirectToLoginWhenAnonymous() throws Exception {
        mockMvc.perform(get("/qbpmcockpit/instances"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/finished-processes/deleted"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/token"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void preAuthorizeEndpoint_deniesWithoutRoleBpm() throws Exception {
        org.springframework.security.oauth2.jwt.Jwt token = org.springframework.security.oauth2.jwt.Jwt.withTokenValue("t")
                .header("alg", "none")
                .subject("john")
                .claim("roles", List.of("ROLE_USER"))
                .build();
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(token, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(post("/qbpmcockpit/instances/finish")
                        .header("Referer", "/qbpmcockpit/instances")
                        .with(authentication(authentication)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", containsString("error=accessDenied")));
    }

    @Test
    void preAuthorizeEndpoint_allowsWithRoleBpm() throws Exception {
        when(qbpmPlayerService.deleteInstances(any())).thenReturn(List.of());
        when(bpmFinishedProcessService.saveFinishResults(any(), any())).thenReturn(List.of());

        org.springframework.security.oauth2.jwt.Jwt token = org.springframework.security.oauth2.jwt.Jwt.withTokenValue("t")
                .header("alg", "none")
                .subject("john")
                .claim("roles", List.of("ROLE_BPM"))
                .build();
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(token, List.of(new SimpleGrantedAuthority("ROLE_BPM")));

        mockMvc.perform(post("/qbpmcockpit/instances/finish")
                        .param("id", "1")
                        .with(authentication(authentication)))
                .andExpect(status().isOk());
    }

    @Test
    void authLogout_isProtected_requiresAuthentication() throws Exception {
        when(authService.logout(any())).thenReturn(new AuthService.LogoutResult(
                302,
                "/login",
                ResponseCookie.from(CookieBearerTokenResolver.ACCESS_TOKEN_COOKIE, "").path("/").build()
        ));

        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/auth/logout")
                        .with(jwt().jwt(j -> j
                                .subject("john")
                                .claim("jti", "jti")
                                .expiresAt(Instant.now().plusSeconds(60)))))
                .andExpect(status().is3xxRedirection());
    }
}
