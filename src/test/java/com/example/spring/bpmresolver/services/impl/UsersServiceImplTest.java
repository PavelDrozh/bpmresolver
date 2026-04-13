package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.entities.UserAccount;
import com.example.spring.bpmresolver.entities.UserRole;
import com.example.spring.bpmresolver.repositories.UserAccountRepository;
import com.example.spring.bpmresolver.repositories.UserRoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersServiceImplTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private UsersServiceImpl service;

    @Test
    void loadUserByUsername_whenUserNotFound_throws() {
        when(userAccountRepository.findByUsername("john")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("john"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("john");
    }

    @Test
    void loadUserByUsername_buildsUserDetailsWithRolesAndEnabledFlag() {
        UserAccount account = UserAccount.builder()
                .id(10L)
                .username("john")
                .password("pwd")
                .enabled(true)
                .build();

        when(userAccountRepository.findByUsername("john")).thenReturn(Optional.of(account));
        when(userRoleRepository.findAllByUserId(10L)).thenReturn(List.of(
                UserRole.builder().userId(10L).role("ROLE_BPM").build(),
                UserRole.builder().userId(10L).role("ROLE_USER").build()
        ));

        UserDetails details = service.loadUserByUsername("john");

        assertThat(details.getUsername()).isEqualTo("john");
        assertThat(details.getPassword()).isEqualTo("pwd");
        assertThat(details.isEnabled()).isTrue();
        assertThat(details.getAuthorities().stream().map(GrantedAuthority::getAuthority))
                .containsExactlyInAnyOrder("ROLE_BPM", "ROLE_USER");
    }

    @Test
    void loadUserByUsername_whenDisabled_setsDisabledFlag() {
        UserAccount account = UserAccount.builder()
                .id(10L)
                .username("john")
                .password("pwd")
                .enabled(false)
                .build();

        when(userAccountRepository.findByUsername("john")).thenReturn(Optional.of(account));
        when(userRoleRepository.findAllByUserId(10L)).thenReturn(List.of());

        UserDetails details = service.loadUserByUsername("john");

        assertThat(details.isEnabled()).isFalse();
    }
}
