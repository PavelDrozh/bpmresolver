package com.example.spring.bpmresolver.clients;

import com.example.spring.bpmresolver.dto.AccessTokenResponseDto;
import com.example.spring.bpmresolver.localization.LocalizationService;
import lombok.AllArgsConstructor;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import static com.example.spring.bpmresolver.localization.LocalizationServiceImpl.FEIGN_FALLBACK_EXTERNAL_SERVICE_ERROR;

@Component
@AllArgsConstructor
public class AccessTokenClientFallbackFactory implements FallbackFactory<AccessTokenClient> {

    private final LocalizationService localizationService;

    @Override
    public AccessTokenClient create(Throwable cause) {
        int status = FeignFallbackUtils.extractStatus(cause);
        String message = FeignFallbackUtils.buildErrorMessage("accessTokenClient", status);

        return form -> AccessTokenResponseDto.builder()
                .accessToken(localizationService.getMessage(FEIGN_FALLBACK_EXTERNAL_SERVICE_ERROR, status, message))
                .build();
    }
}
