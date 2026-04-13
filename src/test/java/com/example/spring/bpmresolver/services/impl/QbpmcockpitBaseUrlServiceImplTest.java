package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.config.QbpmcockpitProperties;
import com.example.spring.bpmresolver.entities.QbpmcockpitUserSetting;
import com.example.spring.bpmresolver.repositories.QbpmcockpitUserSettingRepository;
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
class QbpmcockpitBaseUrlServiceImplTest {

    @Mock
    private QbpmcockpitUserSettingRepository userSettingRepository;

    @Mock
    private QbpmcockpitProperties properties;

    @InjectMocks
    private QbpmcockpitBaseUrlServiceImpl service;

    @Captor
    private ArgumentCaptor<QbpmcockpitUserSetting> settingCaptor;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getBaseUrlOrNull_prefersDbValue() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("john", "n/a");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userSettingRepository.findByUsername("john")).thenReturn(Optional.of(
                QbpmcockpitUserSetting.builder().username("john").baseUrl(" http://db ").build()
        ));

        assertThat(service.getBaseUrlOrNull()).isEqualTo("http://db");
        verifyNoInteractions(properties);
    }

    @Test
    void setBaseUrl_whenBlank_deletes() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("john", "n/a");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        service.setBaseUrl("   ");

        verify(userSettingRepository).deleteByUsername("john");
        verify(userSettingRepository, never()).save(any());
    }

    @Test
    void setBaseUrl_whenNotBlank_saves() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("john", "n/a");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userSettingRepository.findByUsername("john")).thenReturn(Optional.empty());

        service.setBaseUrl(" http://x ");

        verify(userSettingRepository).save(settingCaptor.capture());
        assertThat(settingCaptor.getValue().getUsername()).isEqualTo("john");
        assertThat(settingCaptor.getValue().getBaseUrl()).isEqualTo("http://x");
    }
}
