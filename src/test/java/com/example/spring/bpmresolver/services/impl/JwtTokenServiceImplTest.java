package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.config.JwtProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenServiceImplTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtTokenServiceImpl service;

    @Captor
    private ArgumentCaptor<JwtEncoderParameters> paramsCaptor;

    @Test
    void issueAccessToken_buildsClaimsWithRolesAndEncodes() {
        when(jwtProperties.issuer()).thenReturn("https://issuer");
        when(jwtProperties.accessTtlSeconds()).thenReturn(60L);

        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("token-value");
        when(jwtEncoder.encode(any())).thenReturn(jwt);

        TestingAuthenticationToken authentication = new TestingAuthenticationToken(
                "john",
                "n/a",
                List.of(new SimpleGrantedAuthority("ROLE_BPM"), new SimpleGrantedAuthority("ROLE_USER"))
        );

        JwtTokenServiceImpl.TokenIssueResult result = service.issueAccessToken(authentication);

        assertThat(result.token()).isEqualTo("token-value");
        assertThat(result.jti()).isNotBlank();
        assertThat(result.expiresAt()).isAfter(Instant.now());

        verify(jwtEncoder).encode(paramsCaptor.capture());
        JwtClaimsSet claims = paramsCaptor.getValue().getClaims();

        assertThat(claims.getIssuer().toString()).isEqualTo("https://issuer");
        assertThat(claims.getSubject()).isEqualTo("john");
        assertThat(claims.getId()).isEqualTo(result.jti());

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.getClaims().get("roles");
        assertThat(roles).containsExactlyInAnyOrder("ROLE_BPM", "ROLE_USER");
    }
}
