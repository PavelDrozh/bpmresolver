package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.dto.LoginRequest;
import com.example.spring.bpmresolver.dto.LoginResponse;
import com.example.spring.bpmresolver.entities.RevokedJwt;
import com.example.spring.bpmresolver.repositories.RevokedJwtRepository;
import com.example.spring.bpmresolver.security.CookieBearerTokenResolver;
import com.example.spring.bpmresolver.services.AuthService;
import com.example.spring.bpmresolver.services.JwtTokenService;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.Instant;

@Service
public class AuthServiceImpl implements AuthService {

    public static final String LOGIN = "/login";
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final RevokedJwtRepository revokedJwtRepository;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtTokenService jwtTokenService,
            RevokedJwtRepository revokedJwtRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.revokedJwtRepository = revokedJwtRepository;
    }

    @Override
    public FormLoginResult loginForm(String username, String password, String redirect) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (AuthenticationException ex) {
            String location = UriComponentsBuilder.fromPath(LOGIN)
                    .queryParam("error", "")
                    .queryParam("redirect", redirect)
                    .build()
                    .toUriString();
            return new FormLoginResult(302, location, null);
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

        String target = sanitizeRedirect(redirect);
        return new FormLoginResult(302, target, cookie);
    }

    @Override
    public LoginResponse loginJson(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        JwtTokenService.TokenIssueResult issued = jwtTokenService.issueAccessToken(authentication);
        long expiresInSeconds = Math.max(0, Duration.between(Instant.now(), issued.expiresAt()).toSeconds());
        return new LoginResponse(issued.token(), expiresInSeconds);
    }

    @Override
    public LogoutResult logout(Jwt jwt) {
        if (jwt == null) {
            return new LogoutResult(302, LOGIN, clearCookie());
        }

        String jti = jwt.getId();
        Instant expiresAt = jwt.getExpiresAt();
        if (jti == null || expiresAt == null) {
            return new LogoutResult(302, LOGIN, clearCookie());
        }

        RevokedJwt revoked = RevokedJwt.builder()
                .jti(jti)
                .userId(null)
                .revokedAt(Instant.now())
                .expiresAt(expiresAt)
                .build();
        revokedJwtRepository.save(revoked);

        return new LogoutResult(302, LOGIN, clearCookie());
    }

    private static String sanitizeRedirect(String redirect) {
        String target = (redirect == null || redirect.isBlank()) ? "/" : redirect;
        if (target.startsWith("/api/")) {
            return "/";
        }
        return target;
    }

    private static ResponseCookie clearCookie() {
        return ResponseCookie.from(CookieBearerTokenResolver.ACCESS_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();
    }
}
