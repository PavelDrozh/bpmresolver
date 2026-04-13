package com.example.spring.bpmresolver.repositories;

import com.example.spring.bpmresolver.entities.UserAccount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserAccountRepositoryCrudTest {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Test
    void save_find_update_delete() {
        var saved = userAccountRepository.save(UserAccount.builder()
                .username("u1")
                .password("p1")
                .enabled(true)
                .build());

        assertThat(saved.getId()).isNotNull();

        Optional<UserAccount> found = userAccountRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("u1");

        saved.setPassword("p2");
        userAccountRepository.save(saved);

        var updated = userAccountRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getPassword()).isEqualTo("p2");

        userAccountRepository.deleteById(saved.getId());
        assertThat(userAccountRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void findByUsername_returnsEntity() {
        userAccountRepository.save(UserAccount.builder()
                .username("u2")
                .password("p")
                .enabled(false)
                .build());

        assertThat(userAccountRepository.findByUsername("u2")).isPresent();
        assertThat(userAccountRepository.findByUsername("missing")).isEmpty();
    }
}
