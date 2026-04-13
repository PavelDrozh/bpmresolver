package com.example.spring.bpmresolver.controllers.rest;

import com.example.spring.bpmresolver.dto.LoginRequest;
import com.example.spring.bpmresolver.dto.LoginResponse;
import com.example.spring.bpmresolver.entities.RevokedJwt;
import com.example.spring.bpmresolver.repositories.RevokedJwtRepository;
import com.example.spring.bpmresolver.services.JwtTokenService;
import com.example.spring.bpmresolver.security.CookieBearerTokenResolver;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final RevokedJwtRepository revokedJwtRepository;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtTokenService jwtTokenService,
            RevokedJwtRepository revokedJwtRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.revokedJwtRepository = revokedJwtRepository;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void loginForm(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam(value = "redirect", required = false) String redirect,
            HttpServletResponse response
    ) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (AuthenticationException ex) {
            String location = UriComponentsBuilder.fromPath("/login")
                    .queryParam("error", "")
                    .queryParam("redirect", redirect)
                    .build()
                    .toUriString();
            response.setStatus(302);
            response.setHeader("Location", location);
            return;
        }

        JwtTokenService.TokenIssueResult issued = jwtTokenService.issueAccessToken(authentication);
        long maxAge = Math.max(0, Duration.between(Instant.now(), issued.expiresAt()).toSeconds());

        ResponseCookie cookie = ResponseCookie.from(CookieBearerTokenResolver.ACCESS_TOKEN_COOKIE, issued.token())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(maxAge)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        String target = (redirect == null || redirect.isBlank()) ? "/" : redirect;
        if (target.startsWith("/api/")) {
            target = "/";
        }
        response.setStatus(302);
        response.setHeader("Location", target);
    }

    @PostMapping(value = "/login-json", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginResponse loginJson(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        JwtTokenService.TokenIssueResult issued = jwtTokenService.issueAccessToken(authentication);
        long expiresInSeconds = Math.max(0, Duration.between(Instant.now(), issued.expiresAt()).toSeconds());
        return new LoginResponse(issued.token(), expiresInSeconds);
    }

    @PostMapping(value = "/logout")
    public void logout(
            @AuthenticationPrincipal Jwt jwt,
            HttpServletResponse response
    ) {
        if (jwt == null) {
            clearCookie(response);
            response.setStatus(302);
            response.setHeader("Location", "/login");
            return;
        }
        String jti = jwt.getId();
        Instant expiresAt = jwt.getExpiresAt();
        if (jti == null || expiresAt == null) {
            clearCookie(response);
            response.setStatus(302);
            response.setHeader("Location", "/login");
            return;
        }

        RevokedJwt revoked = RevokedJwt.builder()
                .jti(jti)
                .userId(null)
                .revokedAt(Instant.now())
                .expiresAt(expiresAt)
                .build();
        revokedJwtRepository.save(revoked);

        clearCookie(response);
        response.setStatus(302);
        response.setHeader("Location", "/login");
    }

    private static void clearCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(CookieBearerTokenResolver.ACCESS_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}
