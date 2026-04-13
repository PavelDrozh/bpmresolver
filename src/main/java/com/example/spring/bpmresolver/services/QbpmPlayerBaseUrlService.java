package com.example.spring.bpmresolver.services;

public interface QbpmPlayerBaseUrlService {
    String getBaseUrlOrNull();

    String getContextOrNull();

    void setBaseUrl(String baseUrl);

    void setContext(String context);
}
