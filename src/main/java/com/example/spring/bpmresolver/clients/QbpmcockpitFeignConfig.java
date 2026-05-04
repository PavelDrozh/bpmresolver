package com.example.spring.bpmresolver.clients;

import com.example.spring.bpmresolver.services.ResolverTokenService;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class QbpmcockpitFeignConfig {

    @Bean
    public RequestInterceptor qbpmcockpitRequestInterceptor(ResolverTokenService resolverTokenService) {
        return requestTemplate -> {
            String token = resolverTokenService.getToken();
            if (token != null && !token.isBlank()) {
                requestTemplate.header("Authorization", "Bearer " + token);
            }
        };
    }
}
