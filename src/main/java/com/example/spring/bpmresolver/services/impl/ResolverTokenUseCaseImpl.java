package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.dto.AccessTokenResponseDto;
import com.example.spring.bpmresolver.dto.TokenRequestDto;
import com.example.spring.bpmresolver.services.AccessTokenService;
import com.example.spring.bpmresolver.services.ResolverAccessTokenService;
import com.example.spring.bpmresolver.services.ResolverTokenUseCase;
import com.example.spring.bpmresolver.services.ResolverTokenService;
import org.springframework.stereotype.Service;

@Service
public class ResolverTokenUseCaseImpl implements ResolverTokenUseCase {

    private final ResolverTokenService resolverTokenService;
    private final AccessTokenService accessTokenService;
    private final ResolverAccessTokenService resolverAccessTokenService;

    public ResolverTokenUseCaseImpl(
            ResolverTokenService resolverTokenService,
            AccessTokenService accessTokenService,
            ResolverAccessTokenService resolverAccessTokenService
    ) {
        this.resolverTokenService = resolverTokenService;
        this.accessTokenService = accessTokenService;
        this.resolverAccessTokenService = resolverAccessTokenService;
    }

    @Override
    public TokenPageData getTokenPageData(boolean updated, boolean passwordRequired) {
        TokenRequestDto tokenRequest = TokenRequestDto.builder()
                .realm(resolverAccessTokenService.getRealm())
                .clientId(resolverAccessTokenService.getClientId())
                .userName(resolverAccessTokenService.getUserName())
                .url(resolverAccessTokenService.getUrl())
                .build();

        return new TokenPageData(
                resolverTokenService.getToken(),
                tokenRequest,
                updated,
                passwordRequired
        );
    }

    @Override
    public void updateToken(String token) {
        resolverTokenService.setToken(token);
    }

    @Override
    public AutoUpdateResult autoUpdateToken(TokenRequestDto tokenRequest) {
        if (tokenRequest == null || tokenRequest.getPassword() == null || tokenRequest.getPassword().isBlank()) {
            return AutoUpdateResult.PASSWORD_REQUIRED;
        }

        resolverAccessTokenService.updateAccessSettings(tokenRequest);
        AccessTokenResponseDto accessTokenResponse = accessTokenService.getAccessTokenForPasswordGrant(
                resolverAccessTokenService.getUserName(),
                tokenRequest.getPassword(),
                resolverAccessTokenService.getClientId()
        );
        resolverTokenService.setToken(accessTokenResponse.getAccessToken());
        return AutoUpdateResult.UPDATED;
    }
}
