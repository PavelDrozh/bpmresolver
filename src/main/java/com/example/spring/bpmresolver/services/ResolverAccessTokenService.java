package com.example.spring.bpmresolver.services;

import com.example.spring.bpmresolver.dto.TokenRequestDto;

public interface ResolverAccessTokenService {
    String getRealm();

    String getClientId();

    String getUserName();

    String getUrl();

    void updateAccessSettings(TokenRequestDto settings);
}
