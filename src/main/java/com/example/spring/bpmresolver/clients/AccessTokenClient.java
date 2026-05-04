package com.example.spring.bpmresolver.clients;

import com.example.spring.bpmresolver.dto.AccessTokenResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "accessTokenClient",
        url = "${app.auth.access.url}",
        configuration = AccessTokenClientConfig.class,
        fallbackFactory = AccessTokenClientFallbackFactory.class
)
public interface AccessTokenClient {

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    AccessTokenResponseDto getAccessToken(
            @RequestBody MultiValueMap<String, String> form
    );

}
