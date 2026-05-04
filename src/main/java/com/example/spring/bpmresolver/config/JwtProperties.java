package com.example.spring.bpmresolver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String issuer,
        long accessTtlSeconds,
        String publicKeyPem,
        String privateKeyPem,
        String publicKeyLocation,
        String privateKeyLocation
) {
}
