package com.example.spring.bpmresolver.clients;

import com.example.spring.bpmresolver.config.RoutingProperties;
import com.example.spring.bpmresolver.localization.LocalizationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

import static com.example.spring.bpmresolver.localization.LocalizationServiceImpl.*;

@Component
@AllArgsConstructor
public class ServiceIdValidator {

    private static final Pattern K8S_SERVICE_ID_PATTERN = Pattern.compile("^[a-z0-9]([-a-z0-9]*[a-z0-9])?$");

    private final RoutingProperties routingProperties;
    private final LocalizationService localizationService;

    public String validateQbpmplayerServiceIdOrThrow(String serviceId) {
        return validateOrThrow(serviceId, routingProperties.getQbpmplayerAllowedServiceIds(), "qbpmplayer");
    }

    public String validateContextOrThrow(String context) {
        if (context == null || context.isBlank()) {
            throw new IllegalArgumentException(localizationService.getMessage(VALIDATION_CONTEXT_BLANK));
        }
        if (!K8S_SERVICE_ID_PATTERN.matcher(context).matches()) {
            throw new IllegalArgumentException(localizationService.getMessage(VALIDATION_INVALID_CONTEXT, context));
        }
        return context;
    }

    private String validateOrThrow(String serviceId, List<String> allowList, String label) {
        if (serviceId == null || serviceId.isBlank()) {
            throw new IllegalArgumentException(localizationService.getMessage(VALIDATION_SERVICE_ID_BLANK));
        }
        if (!K8S_SERVICE_ID_PATTERN.matcher(serviceId).matches()) {
            throw new IllegalArgumentException(localizationService.getMessage(VALIDATION_INVALID_SERVICE_ID, label, serviceId));
        }
        if (allowList != null && !allowList.isEmpty() && !allowList.contains(serviceId)) {
            throw new IllegalArgumentException(localizationService.getMessage(VALIDATION_SERVICE_ID_NOT_ALLOWED, label, serviceId));
        }
        return serviceId;
    }
}
