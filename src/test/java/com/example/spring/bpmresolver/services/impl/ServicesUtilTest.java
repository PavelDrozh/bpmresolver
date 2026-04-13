package com.example.spring.bpmresolver.services.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ServicesUtilTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void isBlank() {
        assertThat(ServicesUtil.isBlank(null)).isTrue();
        assertThat(ServicesUtil.isBlank("")).isTrue();
        assertThat(ServicesUtil.isBlank("   ")).isTrue();
        assertThat(ServicesUtil.isBlank("a")).isFalse();
    }

    @Test
    void normalize() {
        assertThat(ServicesUtil.normalize(null)).isNull();
        assertThat(ServicesUtil.normalize("  ")).isNull();
        assertThat(ServicesUtil.normalize(" a ")).isEqualTo("a");
    }

    @Test
    void getFromDb_emptyOptional_returnsNull() {
        assertThat(ServicesUtil.getFromDb(Optional.empty(), Object::toString)).isNull();
    }

    @Test
    void getFromDb_normalizesValue() {
        record R(String value) {}
        assertThat(ServicesUtil.getFromDb(Optional.of(new R("  x  ")), R::value)).isEqualTo("x");
        assertThat(ServicesUtil.getFromDb(Optional.of(new R("   ")), R::value)).isNull();
    }

    @Test
    void getCurrentUsernameOrNull_whenNoAuthentication_returnsNull() {
        assertThat(ServicesUtil.getCurrentUsernameOrNull()).isNull();
    }

    @Test
    void getCurrentUsernameOrNull_whenPrincipalIsString_returnsString() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("john", "n/a");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        assertThat(ServicesUtil.getCurrentUsernameOrNull()).isEqualTo("john");
    }

    @Test
    void getCurrentUsernameOrNull_whenPrincipalIsJwt_returnsSubject() {
        Jwt jwt = Jwt.withTokenValue("t")
                .header("alg", "none")
                .claim("sub", "ignored")
                .subject("subj")
                .build();
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(jwt, "n/a");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        assertThat(ServicesUtil.getCurrentUsernameOrNull()).isEqualTo("subj");
    }

    @Test
    void getCurrentUsernameOrNull_whenAnonymousUser_returnsNull() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("anonymousUser", "n/a");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        assertThat(ServicesUtil.getCurrentUsernameOrNull()).isNull();
    }
}
