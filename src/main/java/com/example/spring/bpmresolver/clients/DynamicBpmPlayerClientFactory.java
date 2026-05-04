package com.example.spring.bpmresolver.clients;

import com.example.spring.bpmresolver.localization.LocalizationService;
import lombok.AllArgsConstructor;
import org.springframework.cloud.openfeign.FeignClientBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

import static com.example.spring.bpmresolver.localization.LocalizationServiceImpl.VALIDATION_SERVICE_ID_BLANK;

@Component
@AllArgsConstructor
public class DynamicBpmPlayerClientFactory {

    private final ApplicationContext applicationContext;
    private final LocalizationService localizationService;
    private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();

    public QbpmPlayerClient getClient(String serviceId) {
        if (serviceId == null || serviceId.isBlank()) {
            throw new IllegalArgumentException(localizationService.getMessage(VALIDATION_SERVICE_ID_BLANK));
        }
        String key = QbpmPlayerClient.class.getName() + "::" + serviceId;
        return (QbpmPlayerClient) cache.computeIfAbsent(key, k ->
             new FeignClientBuilder(applicationContext)
                     .forType(QbpmPlayerClient.class, serviceId)
                     .fallback(QbpmPlayerClientFallback.class)
                     .build()
        );
    }
}
