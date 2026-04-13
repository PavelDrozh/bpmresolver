package com.example.spring.bpmresolver.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "revoked_jwt")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevokedJwt {

    @Id
    @Column(name = "jti", nullable = false, length = 64)
    private String jti;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "revoked_at", nullable = false)
    private Instant revokedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
}
