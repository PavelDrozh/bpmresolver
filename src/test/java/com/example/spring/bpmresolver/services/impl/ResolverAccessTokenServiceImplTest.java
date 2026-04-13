package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.config.AuthAccessProperties;
import com.example.spring.bpmresolver.dto.TokenRequestDto;
import com.example.spring.bpmresolver.entities.ResolverAccessUserSetting;
import com.example.spring.bpmresolver.repositories.ResolverAccessUserSettingRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResolverAccessTokenServiceImplTest {

    @Mock
    private ResolverAccessUserSettingRepository userSettingRepository;

    @Mock
    private AuthAccessProperties properties;

    @InjectMocks
    private ResolverAccessTokenServiceImpl service;

    @Captor
    private ArgumentCaptor<ResolverAccessUserSetting> settingCaptor;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getUrl_prefersDbValue() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("john", "n/a");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userSettingRepository.findByUsername("john")).thenReturn(Optional.of(
                ResolverAccessUserSetting.builder().username("john").url(" http://db ").build()
        ));

        assertThat(service.getUrl()).isEqualTo("http://db");
        verifyNoInteractions(properties);
    }

    @Test
    void getClientId_fallsBackToProperties() {
        when(properties.getClientId()).thenReturn("  c1 ");
        assertThat(service.getClientId()).isEqualTo("c1");
    }

    @Test
    void updateAccessSettings_whenNull_doesNothing() {
        service.updateAccessSettings(null);
        verifyNoInteractions(userSettingRepository);
    }

    @Test
    void updateAccessSettings_whenNoUser_doesNothing() {
        service.updateAccessSettings(TokenRequestDto.builder().realm("r").build());
        verifyNoInteractions(userSettingRepository);
    }

    @Test
    void updateAccessSettings_whenAllBlank_deletes() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("john", "n/a");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userSettingRepository.findByUsername("john")).thenReturn(Optional.of(
                ResolverAccessUserSetting.builder().username("john").build()
        ));

        service.updateAccessSettings(TokenRequestDto.builder()
                .realm("  ")
                .clientId(null)
                .userName(" ")
                .url("")
                .build());

        verify(userSettingRepository).deleteByUsername("john");
        verify(userSettingRepository, never()).save(any());
    }

    @Test
    void updateAccessSettings_savesNormalizedValues() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("john", "n/a");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userSettingRepository.findByUsername("john")).thenReturn(Optional.empty());

        service.updateAccessSettings(TokenRequestDto.builder()
                .realm(" r ")
                .clientId(" c ")
                .userName(" u ")
                .url(" http://x ")
                .build());

        verify(userSettingRepository).save(settingCaptor.capture());
        ResolverAccessUserSetting setting = settingCaptor.getValue();
        assertThat(setting.getUsername()).isEqualTo("john");
        assertThat(setting.getRealm()).isEqualTo("r");
        assertThat(setting.getClientId()).isEqualTo("c");
        assertThat(setting.getAccessUser()).isEqualTo("u");
        assertThat(setting.getUrl()).isEqualTo("http://x");
    }
}
