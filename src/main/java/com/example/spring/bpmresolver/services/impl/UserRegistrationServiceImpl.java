package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.entities.UserAccount;
import com.example.spring.bpmresolver.entities.UserRole;
import com.example.spring.bpmresolver.repositories.UserAccountRepository;
import com.example.spring.bpmresolver.repositories.UserRoleRepository;
import com.example.spring.bpmresolver.services.UserRegistrationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private final UserAccountRepository userAccountRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationServiceImpl(UserAccountRepository userAccountRepository,
                                       UserRoleRepository userRoleRepository,
                                       PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void register(String username, String password, String passwordConfirm) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Логин обязателен");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Пароль обязателен");
        }
        if (!password.equals(passwordConfirm)) {
            throw new IllegalArgumentException("Пароли не совпадают");
        }

        if (userAccountRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким логином уже существует");
        }

        UserAccount saved = userAccountRepository.save(UserAccount.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .build());

        userRoleRepository.save(UserRole.builder()
                .userId(saved.getId())
                .role("ROLE_USER")
                .build());
    }
}
