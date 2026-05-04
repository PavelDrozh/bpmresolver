package com.example.spring.bpmresolver.services;

import com.example.spring.bpmresolver.dto.TokenRequestDto;

public interface ResolverTokenUseCase {

    TokenPageData getTokenPageData(boolean updated, boolean passwordRequired);

    void updateToken(String token);

    AutoUpdateResult autoUpdateToken(TokenRequestDto tokenRequest);

    record TokenPageData(String token, TokenRequestDto tokenRequest, boolean updated, boolean passwordRequired) {
    }

    enum AutoUpdateResult {
        UPDATED,
        PASSWORD_REQUIRED
    }
}
