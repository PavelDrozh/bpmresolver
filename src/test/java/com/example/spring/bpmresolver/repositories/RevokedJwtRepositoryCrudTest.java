package com.example.spring.bpmresolver.repositories;

import com.example.spring.bpmresolver.entities.RevokedJwt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RevokedJwtRepositoryCrudTest {

    @Autowired
    private RevokedJwtRepository revokedJwtRepository;

    @Test
    void save_existsByJtiAndExpiresAtAfter_delete() {
        var now = Instant.now();

        revokedJwtRepository.save(RevokedJwt.builder()
                .jti("jti-1")
                .userId(10L)
                .revokedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .build());

        assertThat(revokedJwtRepository.existsByJtiAndExpiresAtAfter("jti-1", now)).isTrue();
        assertThat(revokedJwtRepository.existsByJtiAndExpiresAtAfter("jti-1", now.plusSeconds(7200))).isFalse();

        revokedJwtRepository.deleteById("jti-1");
        assertThat(revokedJwtRepository.findById("jti-1")).isEmpty();
    }
}
