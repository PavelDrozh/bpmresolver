package com.example.spring.bpmresolver.clients;

import com.example.spring.bpmresolver.services.QbpmPlayerServiceNameService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QbpmPlayerClientRouter {

    private final DynamicBpmPlayerClientFactory dynamicBpmPlayerClientFactory;
    private final QbpmPlayerServiceNameService qbpmPlayerServiceNameService;
    private final ServiceIdValidator serviceIdValidator;
    private final QbpmPlayerClientFallback fallback;
    @Value("${app.qbpmplayer.service:qesevaluationsbpm}")
    private String serviceName;
    @Value("${app.qbpmplayer.context:qesevaluationsbpm}")
    private String contextName;

    public QbpmPlayerClientRouter(DynamicBpmPlayerClientFactory dynamicBpmPlayerClientFactory, QbpmPlayerServiceNameService qbpmPlayerServiceNameService, ServiceIdValidator serviceIdValidator, QbpmPlayerClientFallback fallback) {
        this.dynamicBpmPlayerClientFactory = dynamicBpmPlayerClientFactory;
        this.qbpmPlayerServiceNameService = qbpmPlayerServiceNameService;
        this.serviceIdValidator = serviceIdValidator;
        this.fallback = fallback;
    }

    public QbpmPlayerClient client() {
        String serviceId = qbpmPlayerServiceNameService.getServiceNameOrNull();
        if (serviceId == null || serviceId.isBlank()) {
            serviceId = serviceName;
        }
        try {
            serviceId = serviceIdValidator.validateQbpmplayerServiceIdOrThrow(serviceId);
        } catch (RuntimeException e) {
            serviceId = serviceName;
        }
        QbpmPlayerClient client;
        try {
            client = dynamicBpmPlayerClientFactory.getClient(serviceId);
        } catch (RuntimeException e) {
            client = fallback;
        }
        return client;
    }

    public String context() {
        String context = qbpmPlayerServiceNameService.getContextOrNull();
        if (context == null || context.isBlank()) {
            context = contextName;
        }
        try {
            return serviceIdValidator.validateContextOrThrow(context);
        } catch (RuntimeException e) {
            return contextName;
        }
    }
}
