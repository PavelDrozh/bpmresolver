package com.example.spring.bpmresolver.repositories;

import com.example.spring.bpmresolver.entities.QbpmplayerUserSetting;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class QbpmplayerUserSettingRepositoryCrudTest {

    @Autowired
    private QbpmplayerUserSettingRepository repository;

    @Test
    void save_findByUsername_update_deleteByUsername() {
        var saved = repository.save(QbpmplayerUserSetting.builder()
                .username("u1")
                .baseUrl("http://a")
                .context("ctx")
                .build());

        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findByUsername("u1")).isPresent();

        saved.setContext("ctx2");
        repository.save(saved);

        var updated = repository.findByUsername("u1").orElseThrow();
        assertThat(updated.getContext()).isEqualTo("ctx2");

        repository.deleteByUsername("u1");
        assertThat(repository.findByUsername("u1")).isEmpty();
    }
}
