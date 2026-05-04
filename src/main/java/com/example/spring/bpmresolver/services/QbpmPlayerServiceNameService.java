package com.example.spring.bpmresolver.services;

public interface QbpmPlayerServiceNameService {
    String getServiceNameOrNull();

    String getContextOrNull();

    void setServiceName(String baseUrl);

    void setContext(String context);
}
