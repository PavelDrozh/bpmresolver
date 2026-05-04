package com.example.spring.bpmresolver.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "resolver_access_user_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResolverAccessUserSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "realm")
    private String realm;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "access_user")
    private String accessUser;

    @Column(name = "url")
    private String url;
}
