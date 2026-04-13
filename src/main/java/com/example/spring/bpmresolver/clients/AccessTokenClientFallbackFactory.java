package com.example.spring.bpmresolver.clients;

import com.example.spring.bpmresolver.dto.AccessTokenResponseDto;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class AccessTokenClientFallbackFactory implements FallbackFactory<AccessTokenClient> {

    @Override
    public AccessTokenClient create(Throwable cause) {
        int status = FeignFallbackUtils.extractStatus(cause);
        String message = FeignFallbackUtils.buildErrorMessage("accessTokenClient", status);

        return form -> AccessTokenResponseDto.builder()
                .accessToken(String.format("Получена ошибка от внешнегос сервиса со статусом %d и сообщением %s", status, message))
                .build();
    }
}
