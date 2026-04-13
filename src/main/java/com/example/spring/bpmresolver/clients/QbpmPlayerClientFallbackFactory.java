package com.example.spring.bpmresolver.clients;

import com.example.spring.bpmresolver.dto.BpmFinishedProcessResponseDto;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class QbpmPlayerClientFallbackFactory implements FallbackFactory<QbpmPlayerClient> {

    @Override
    public QbpmPlayerClient create(Throwable cause) {
        int status = FeignFallbackUtils.extractStatus(cause);
        String message = FeignFallbackUtils.buildErrorMessage("qbpmPlayerClient", status);

        return ids -> List.of(BpmFinishedProcessResponseDto.builder()
                        .id(UUID.randomUUID().toString())
                    .status("ERROR")
                    .message(message)
                    .build());
    }
}
