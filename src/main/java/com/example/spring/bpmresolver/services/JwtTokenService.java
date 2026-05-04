package com.example.spring.bpmresolver.services;

import org.springframework.security.core.Authentication;

import java.time.Instant;

public interface JwtTokenService {

    TokenIssueResult issueAccessToken(Authentication authentication);

    record TokenIssueResult(String token, Instant expiresAt, String jti) {
    }
}
