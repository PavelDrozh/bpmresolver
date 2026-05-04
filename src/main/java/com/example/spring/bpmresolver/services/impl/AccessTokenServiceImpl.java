package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.clients.AccessTokenClient;
import com.example.spring.bpmresolver.dto.AccessTokenResponseDto;
import com.example.spring.bpmresolver.services.AccessTokenService;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class AccessTokenServiceImpl implements AccessTokenService {

    private final AccessTokenClient accessTokenClient;

    public AccessTokenServiceImpl(AccessTokenClient accessTokenClient) {
        this.accessTokenClient = accessTokenClient;
    }

    @Override
    public AccessTokenResponseDto getAccessTokenForPasswordGrant(
            String username,
            String password,
            String clientId
    ) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("username", username);
        form.add("password", password);
        form.add("client_id", clientId);
        return accessTokenClient.getAccessToken(form);
    }
}
