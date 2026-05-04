package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.clients.AccessTokenClient;
import com.example.spring.bpmresolver.dto.AccessTokenResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessTokenServiceImplTest {

    @Mock
    private AccessTokenClient accessTokenClient;

    @InjectMocks
    private AccessTokenServiceImpl service;

    @Captor
    private ArgumentCaptor<MultiValueMap<String, String>> formCaptor;

    @Test
    void getAccessTokenForPasswordGrant_buildsFormAndDelegatesToClient() {
        AccessTokenResponseDto expected = new AccessTokenResponseDto();
        when(accessTokenClient.getAccessToken(org.mockito.ArgumentMatchers.any())).thenReturn(expected);

        AccessTokenResponseDto actual = service.getAccessTokenForPasswordGrant("user", "pass", "client");

        assertThat(actual).isSameAs(expected);

        verify(accessTokenClient).getAccessToken(formCaptor.capture());
        MultiValueMap<String, String> form = formCaptor.getValue();
        assertThat(form.getFirst("grant_type")).isEqualTo("password");
        assertThat(form.getFirst("username")).isEqualTo("user");
        assertThat(form.getFirst("password")).isEqualTo("pass");
        assertThat(form.getFirst("client_id")).isEqualTo("client");
    }
}
