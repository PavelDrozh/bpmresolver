package com.example.spring.bpmresolver.repositories;

import com.example.spring.bpmresolver.entities.ResolverTokenUserSetting;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ResolverTokenUserSettingRepositoryCrudTest {

    @Autowired
    private ResolverTokenUserSettingRepository repository;

    @Test
    void save_findByUsername_update_deleteByUsername() {
        var saved = repository.save(ResolverTokenUserSetting.builder()
                .username("u1")
                .token("t1")
                .build());

        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findByUsername("u1")).isPresent();

        saved.setToken("t2");
        repository.save(saved);

        var updated = repository.findByUsername("u1").orElseThrow();
        assertThat(updated.getToken()).isEqualTo("t2");

        repository.deleteByUsername("u1");
        assertThat(repository.findByUsername("u1")).isEmpty();
    }
}
