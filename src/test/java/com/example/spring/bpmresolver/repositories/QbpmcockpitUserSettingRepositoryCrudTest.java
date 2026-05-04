package com.example.spring.bpmresolver.repositories;

import com.example.spring.bpmresolver.entities.QbpmcockpitUserSetting;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class QbpmcockpitUserSettingRepositoryCrudTest {

    @Autowired
    private QbpmcockpitUserSettingRepository repository;

    @Test
    void save_findByUsername_update_deleteByUsername() {
        var saved = repository.save(QbpmcockpitUserSetting.builder()
                .username("u1")
                .baseUrl("http://a")
                .build());

        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findByUsername("u1")).isPresent();

        saved.setBaseUrl("http://b");
        repository.save(saved);

        var updated = repository.findByUsername("u1").orElseThrow();
        assertThat(updated.getBaseUrl()).isEqualTo("http://b");

        repository.deleteByUsername("u1");
        assertThat(repository.findByUsername("u1")).isEmpty();
    }
}
