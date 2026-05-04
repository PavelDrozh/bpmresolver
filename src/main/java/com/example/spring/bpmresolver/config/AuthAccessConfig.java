package com.example.spring.bpmresolver.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AuthAccessProperties.class)
public class AuthAccessConfig {
}