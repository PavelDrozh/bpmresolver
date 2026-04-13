package com.example.spring.bpmresolver.clients;

import com.example.spring.bpmresolver.services.QbpmPlayerBaseUrlService;
import com.example.spring.bpmresolver.services.ResolverTokenService;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class QbpmPlayerConfig {


    @Bean
    public RequestInterceptor qbpmPlayerRequestInterceptor(ResolverTokenService resolverTokenService,
                                                            QbpmPlayerBaseUrlService qbpmPlayerBaseUrlService) {
        return requestTemplate -> {
            String baseUrl = qbpmPlayerBaseUrlService.getBaseUrlOrNull();
            String context = qbpmPlayerBaseUrlService.getContextOrNull();
            if (baseUrl != null && context != null) {
                requestTemplate.target(baseUrl + "/" + context);
            }

            String token = resolverTokenService.getToken();
            if (token != null && !token.isBlank()) {
                requestTemplate.header("Authorization", "Bearer " + token);
            }
        };
    }
}

