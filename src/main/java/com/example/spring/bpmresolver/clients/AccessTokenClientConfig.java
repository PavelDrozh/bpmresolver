package com.example.spring.bpmresolver.clients;

import com.example.spring.bpmresolver.services.ResolverAccessTokenService;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class AccessTokenClientConfig {

    @Bean
    public RequestInterceptor acessTokenRequestInterceptor(ResolverAccessTokenService resolverAccessTokenService) {
        return requestTemplate -> {
            String baseUrl = resolverAccessTokenService.getUrl();
            if (baseUrl != null) {
                requestTemplate.target(baseUrl);
            }
        };
    }
}
