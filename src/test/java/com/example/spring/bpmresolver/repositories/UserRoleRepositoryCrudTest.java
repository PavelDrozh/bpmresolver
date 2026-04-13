package com.example.spring.bpmresolver.repositories;

import com.example.spring.bpmresolver.entities.UserAccount;
import com.example.spring.bpmresolver.entities.UserRole;
import com.example.spring.bpmresolver.entities.UserRoleId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRoleRepositoryCrudTest {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Test
    void save_find_delete_withCompositeId() {
        var user = userAccountRepository.save(UserAccount.builder()
                .username("u-role")
                .password("p")
                .enabled(true)
                .build());

        var savedRole = userRoleRepository.save(UserRole.builder()
                .userId(user.getId())
                .role("ADMIN")
                .build());

        var id = new UserRoleId(savedRole.getUserId(), savedRole.getRole());

        assertThat(userRoleRepository.findById(id)).isPresent();
        assertThat(userRoleRepository.findAllByUserId(user.getId())).hasSize(1);

        userRoleRepository.deleteById(id);
        assertThat(userRoleRepository.findById(id)).isEmpty();
    }
}
