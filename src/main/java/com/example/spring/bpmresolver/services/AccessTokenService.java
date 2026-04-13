package com.example.spring.bpmresolver.services;

import com.example.spring.bpmresolver.dto.AccessTokenResponseDto;

public interface AccessTokenService {

    AccessTokenResponseDto getAccessTokenForPasswordGrant(
            String username,
            String password,
            String clientId
    );
}
