package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.clients.ServiceIdValidator;
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
class QbpmPlayerServiceNameServiceImplTest {

    @Mock
    private QbpmplayerUserSettingRepository userSettingRepository;

    @Mock
    private QbpmPlayerProperties properties;

    @Mock
    private ServiceIdValidator serviceIdValidator;

    @InjectMocks
    private QbpmPlayerServiceNameServiceImpl service;

    @Captor
    private ArgumentCaptor<QbpmplayerUserSetting> settingCaptor;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getServiceNameOrNull_whenUserHasValueInDb_returnsDbValue() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("john", "n/a");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userSettingRepository.findByUsername("john")).thenReturn(Optional.of(
                QbpmplayerUserSetting.builder().username("john").baseUrl("  http://db  ").build()
        ));

        assertThat(service.getServiceNameOrNull()).isEqualTo("http://db");
        verifyNoInteractions(properties);
    }

    @Test
    void getServiceNameOrNull_whenNoDbValue_usesDefaultAndNormalizesBlankToNull() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("john", "n/a");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userSettingRepository.findByUsername("john")).thenReturn(Optional.empty());
        when(properties.getService()).thenReturn("   ");

        assertThat(service.getServiceNameOrNull()).isNull();
    }

    @Test
    void getContextOrNull_whenNoAuth_usesDefault() {
        when(properties.getContext()).thenReturn("  ctx  ");
        assertThat(service.getContextOrNull()).isEqualTo("ctx");
        verifyNoInteractions(userSettingRepository);
    }

    @Test
    void setServiceName_whenNoAuth_doesNothing() {
        service.setServiceName("http://x");
        verifyNoInteractions(userSettingRepository);
    }

    @Test
    void setBaseUrl_whenBothServiceNameAndContextBlank_deletesByUsername() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("john", "n/a");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userSettingRepository.findByUsername("john")).thenReturn(Optional.of(
                QbpmplayerUserSetting.builder().username("john").baseUrl(null).context(null).build()
        ));

        service.setServiceName("   ");

        verify(userSettingRepository).deleteByUsername("john");
        verify(userSettingRepository, never()).save(any());
    }

    @Test
    void setContext_savesSetting() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("john", "n/a");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userSettingRepository.findByUsername("john")).thenReturn(Optional.empty());

        when(serviceIdValidator.validateContextOrThrow("/ctx")).thenReturn("/ctx");

        service.setContext("  /ctx ");

        verify(userSettingRepository).save(settingCaptor.capture());
        assertThat(settingCaptor.getValue().getUsername()).isEqualTo("john");
        assertThat(settingCaptor.getValue().getContext()).isEqualTo("/ctx");
    }
}
