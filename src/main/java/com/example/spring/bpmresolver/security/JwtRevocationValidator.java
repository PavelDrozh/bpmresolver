package com.example.spring.bpmresolver.security;

import com.example.spring.bpmresolver.repositories.RevokedJwtRepository;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class JwtRevocationValidator implements OAuth2TokenValidator<Jwt> {

    private final RevokedJwtRepository revokedJwtRepository;

    public JwtRevocationValidator(RevokedJwtRepository revokedJwtRepository) {
        this.revokedJwtRepository = revokedJwtRepository;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        String jti = token.getId();
        if (jti == null || jti.isBlank()) {
            return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Missing jti", null));
        }

        if (revokedJwtRepository.existsByJtiAndExpiresAtAfter(jti, Instant.now())) {
            return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Token revoked", null));
        }

        return OAuth2TokenValidatorResult.success();
    }
}
