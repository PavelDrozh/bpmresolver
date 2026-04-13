package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.entities.UserAccount;
import com.example.spring.bpmresolver.entities.UserRole;
import com.example.spring.bpmresolver.repositories.UserAccountRepository;
import com.example.spring.bpmresolver.repositories.UserRoleRepository;
import com.example.spring.bpmresolver.services.UsersService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersServiceImpl implements UsersService {

    private final UserAccountRepository userAccountRepository;
    private final UserRoleRepository userRoleRepository;

    public UsersServiceImpl(UserAccountRepository userAccountRepository, UserRoleRepository userRoleRepository) {
        this.userAccountRepository = userAccountRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount account = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<UserRole> roles = userRoleRepository.findAllByUserId(account.getId());
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(UserRole::getRole)
                .map(SimpleGrantedAuthority::new)
                .toList();

        return User.withUsername(account.getUsername())
                .password(account.getPassword())
                .authorities(authorities)
                .disabled(!account.isEnabled())
                .build();
    }
}
