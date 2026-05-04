package com.example.spring.bpmresolver.repositories;

import com.example.spring.bpmresolver.entities.RevokedJwt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface RevokedJwtRepository extends JpaRepository<RevokedJwt, String> {
    boolean existsByJtiAndExpiresAtAfter(String jti, Instant now);
}
