package com.example.spring.bpmresolver.repositories;

import com.example.spring.bpmresolver.entities.BpmFinishedProcess;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BpmFinishedProcessRepositoryCrudTest {

    @Autowired
    private BpmFinishedProcessRepository repository;

    @Test
    void save_find_update_delete() {
        var saved = repository.save(BpmFinishedProcess.builder()
                .processInstanceId("pi-1")
                .finishedBy("u")
                .status("OK")
                .message("m")
                .finishedAt(LocalDateTime.now())
                .build());

        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findById(saved.getId())).isPresent();

        saved.setStatus("UPDATED");
        repository.save(saved);

        assertThat(repository.findById(saved.getId()).orElseThrow().getStatus()).isEqualTo("UPDATED");

        repository.deleteById(saved.getId());
        assertThat(repository.findById(saved.getId())).isEmpty();
    }

    @Test
    void customQueries_work() {
        repository.save(BpmFinishedProcess.builder()
                .processInstanceId("pi-2")
                .finishedBy("user1")
                .status("S")
                .message("msg")
                .finishedAt(LocalDateTime.now())
                .build());

        assertThat(repository.findAllByFinishedByOrderByFinishedAtDesc("user1", PageRequest.of(0, 10)).getContent())
                .hasSize(1);

        assertThat(repository.search(
                "user1",
                "pi",
                "S",
                "msg",
                null,
                null,
                PageRequest.of(0, 10)
        ).getContent()).hasSize(1);
    }
}
