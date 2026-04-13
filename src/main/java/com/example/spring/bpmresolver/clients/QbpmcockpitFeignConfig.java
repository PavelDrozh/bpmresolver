package com.example.spring.bpmresolver.clients;

import com.example.spring.bpmresolver.services.QbpmcockpitBaseUrlService;
import com.example.spring.bpmresolver.services.ResolverTokenService;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class QbpmcockpitFeignConfig {

    @Bean
    public RequestInterceptor qbpmcockpitRequestInterceptor(ResolverTokenService resolverTokenService,
                                                            QbpmcockpitBaseUrlService qbpmcockpitBaseUrlService) {
        return requestTemplate -> {
            String baseUrl = qbpmcockpitBaseUrlService.getBaseUrlOrNull();
            if (baseUrl != null) {
                requestTemplate.target(baseUrl);
            }
            String token = resolverTokenService.getToken();
            if (token != null && !token.isBlank()) {
                requestTemplate.header("Authorization", "Bearer " + token);
            }
        };
    }
}
