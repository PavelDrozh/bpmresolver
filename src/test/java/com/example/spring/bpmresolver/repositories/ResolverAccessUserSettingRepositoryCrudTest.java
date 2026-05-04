package com.example.spring.bpmresolver.repositories;

import com.example.spring.bpmresolver.entities.ResolverAccessUserSetting;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ResolverAccessUserSettingRepositoryCrudTest {

    @Autowired
    private ResolverAccessUserSettingRepository repository;

    @Test
    void save_findByUsername_update_deleteByUsername() {
        var saved = repository.save(ResolverAccessUserSetting.builder()
                .username("u1")
                .realm("r")
                .clientId("c")
                .accessUser("a")
                .url("http://a")
                .build());

        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findByUsername("u1")).isPresent();

        saved.setRealm("r2");
        repository.save(saved);

        var updated = repository.findByUsername("u1").orElseThrow();
        assertThat(updated.getRealm()).isEqualTo("r2");

        repository.deleteByUsername("u1");
        assertThat(repository.findByUsername("u1")).isEmpty();
    }
}
