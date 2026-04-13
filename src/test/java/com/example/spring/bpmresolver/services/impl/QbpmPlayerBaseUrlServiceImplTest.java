package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.config.QbpmPlayerProperties;
import com.example.spring.bpmresolver.entities.QbpmplayerUserSetting;
import com.example.spring.bpmresolver.repositories.QbpmplayerUserSettingRepository;
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
class QbpmPlayerBaseUrlServiceImplTest {

    @Mock
    private QbpmplayerUserSettingRepository userSettingRepository;

    @Mock
    private QbpmPlayerProperties properties;

    @InjectMocks
    private QbpmPlayerBaseUrlServiceImpl service;

    @Captor
    private ArgumentCaptor<QbpmplayerUserSetting> settingCaptor;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getBaseUrlOrNull_whenUserHasValueInDb_returnsDbValue() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("john", "n/a");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userSettingRepository.findByUsername("john")).thenReturn(Optional.of(
                QbpmplayerUserSetting.builder().username("john").baseUrl("  http://db  ").build()
        ));

        assertThat(service.getBaseUrlOrNull()).isEqualTo("http://db");
        verifyNoInteractions(properties);
    }

    @Test
    void getBaseUrlOrNull_whenNoDbValue_usesDefaultAndNormalizesBlankToNull() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("john", "n/a");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userSettingRepository.findByUsername("john")).thenReturn(Optional.empty());
        when(properties.getBaseUrl()).thenReturn("   ");

        assertThat(service.getBaseUrlOrNull()).isNull();
    }

    @Test
    void getContextOrNull_whenNoAuth_usesDefault() {
        when(properties.getContext()).thenReturn("  ctx  ");
        assertThat(service.getContextOrNull()).isEqualTo("ctx");
        verifyNoInteractions(userSettingRepository);
    }

    @Test
    void setBaseUrl_whenNoAuth_doesNothing() {
        service.setBaseUrl("http://x");
        verifyNoInteractions(userSettingRepository);
    }

    @Test
    void setBaseUrl_whenBothBaseUrlAndContextBlank_deletesByUsername() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("john", "n/a");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userSettingRepository.findByUsername("john")).thenReturn(Optional.of(
                QbpmplayerUserSetting.builder().username("john").baseUrl(null).context(null).build()
        ));

        service.setBaseUrl("   ");

        verify(userSettingRepository).deleteByUsername("john");
        verify(userSettingRepository, never()).save(any());
    }

    @Test
    void setContext_savesSetting() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("john", "n/a");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userSettingRepository.findByUsername("john")).thenReturn(Optional.empty());

        service.setContext("  /ctx ");

        verify(userSettingRepository).save(settingCaptor.capture());
        assertThat(settingCaptor.getValue().getUsername()).isEqualTo("john");
        assertThat(settingCaptor.getValue().getContext()).isEqualTo("/ctx");
    }
}
