package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.config.QbpmcockpitProperties;
import com.example.spring.bpmresolver.entities.ResolverTokenUserSetting;
import com.example.spring.bpmresolver.repositories.ResolverTokenUserSettingRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResolverTokenServiceImplTest {

    @Mock
    private ResolverTokenUserSettingRepository tokenUserSettingRepository;

    @Captor
    private ArgumentCaptor<ResolverTokenUserSetting> settingCaptor;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getToken_whenNoAuth_returnsTokenFromProperties() {
        QbpmcockpitProperties properties = new QbpmcockpitProperties("http://x", "  t1 ");
        ResolverTokenServiceImpl service = new ResolverTokenServiceImpl(properties, tokenUserSettingRepository);

        assertThat(service.getToken()).isEqualTo("t1");
        verifyNoInteractions(tokenUserSettingRepository);
    }

    @Test
    void getToken_whenUserHasDbValue_returnsDbValue() {
        QbpmcockpitProperties properties = new QbpmcockpitProperties("http://x", "t1");
        ResolverTokenServiceImpl service = new ResolverTokenServiceImpl(properties, tokenUserSettingRepository);

        TestingAuthenticationToken authentication = new TestingAuthenticationToken("john", "n/a");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(tokenUserSettingRepository.findByUsername("john")).thenReturn(Optional.of(
                ResolverTokenUserSetting.builder().username("john").token("  db ").build()
        ));

        assertThat(service.getToken()).isEqualTo("db");
    }

    @Test
    void setToken_whenNoUser_updatesInMemory() {
        QbpmcockpitProperties properties = new QbpmcockpitProperties("http://x", null);
        ResolverTokenServiceImpl service = new ResolverTokenServiceImpl(properties, tokenUserSettingRepository);

        service.setToken("  t2 ");

        assertThat(service.getToken()).isEqualTo("t2");
        verifyNoInteractions(tokenUserSettingRepository);
    }

    @Test
    void setToken_whenNormalizedNull_deletesDbSetting() {
        QbpmcockpitProperties properties = new QbpmcockpitProperties("http://x", "t1");
        ResolverTokenServiceImpl service = new ResolverTokenServiceImpl(properties, tokenUserSettingRepository);

        TestingAuthenticationToken authentication = new TestingAuthenticationToken("john", "n/a");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        service.setToken("   ");

        verify(tokenUserSettingRepository).deleteByUsername("john");
        verify(tokenUserSettingRepository, never()).save(any());
    }

    @Test
    void setToken_whenNotNull_savesSetting() {
        QbpmcockpitProperties properties = new QbpmcockpitProperties("http://x", "t1");
        ResolverTokenServiceImpl service = new ResolverTokenServiceImpl(properties, tokenUserSettingRepository);

        TestingAuthenticationToken authentication = new TestingAuthenticationToken("john", "n/a");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(tokenUserSettingRepository.findByUsername("john")).thenReturn(Optional.empty());

        service.setToken(" t2 ");

        verify(tokenUserSettingRepository).save(settingCaptor.capture());
        assertThat(settingCaptor.getValue().getUsername()).isEqualTo("john");
        assertThat(settingCaptor.getValue().getToken()).isEqualTo("t2");
    }
}
