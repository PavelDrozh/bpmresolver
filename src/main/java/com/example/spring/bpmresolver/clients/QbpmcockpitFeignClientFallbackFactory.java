package com.example.spring.bpmresolver.clients;

import com.example.spring.bpmresolver.dto.BpmInstanceDto;
import com.example.spring.bpmresolver.dto.RestResponsePage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Component
public class QbpmcockpitFeignClientFallbackFactory implements FallbackFactory<QbpmcockpitFeignClient> {

    @Override
    public QbpmcockpitFeignClient create(Throwable cause) {
        log.error("Failed to extract status from cause: {}", cause.getMessage());
        int status = FeignFallbackUtils.extractStatus(cause);
        String message = FeignFallbackUtils.buildErrorMessage("qbpmcockpitClient", status);

        return (processName,
                state,
                serviceName,
                userLogin,
                lastStartDate,
                lastEndDate,
                businessKey,
                isRoot,
                withOpenIncidents,
                tenantId,
                sort,
                page,
                size) -> {
            BpmInstanceDto errRes = BpmInstanceDto.builder()
                    .processName(String.format("Получена ошибка от внешнегос сервиса со статусом %d и сообщением %s", status, message))
                    .build();
            RestResponsePage<BpmInstanceDto> pageResponse = new RestResponsePage<>(List.of(errRes));
            pageResponse.setErrorStatus(status);
            pageResponse.setErrorMessage(message);
            return pageResponse;
        };
    }
}
