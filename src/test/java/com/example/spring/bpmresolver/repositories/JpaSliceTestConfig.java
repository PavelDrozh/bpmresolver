package com.example.spring.bpmresolver.repositories;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootConfiguration
@Configuration
@EntityScan(basePackages = "com.example.spring.bpmresolver.entities")
@EnableJpaRepositories(basePackages = "com.example.spring.bpmresolver.repositories")
public class JpaSliceTestConfig {
}
