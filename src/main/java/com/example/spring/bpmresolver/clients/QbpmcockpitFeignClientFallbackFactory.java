package com.example.spring.bpmresolver.clients;

import com.example.spring.bpmresolver.dto.BpmInstanceDto;
import com.example.spring.bpmresolver.dto.RestResponsePage;
import com.example.spring.bpmresolver.localization.LocalizationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.spring.bpmresolver.localization.LocalizationServiceImpl.FEIGN_FALLBACK_EXTERNAL_SERVICE_ERROR;
import static com.example.spring.bpmresolver.localization.LocalizationServiceImpl.FEIGN_FALLBACK_EXTRACT_STATUS_FAILED;

@Slf4j
@Component
@AllArgsConstructor
public class QbpmcockpitFeignClientFallbackFactory implements FallbackFactory<QbpmcockpitFeignClient> {

    private final LocalizationService localizationService;

    @Override
    public QbpmcockpitFeignClient create(Throwable cause) {
        log.error(localizationService.getMessage(FEIGN_FALLBACK_EXTRACT_STATUS_FAILED, cause.getMessage()));
        int status = FeignFallbackUtils.extractStatus(cause);
        String message = FeignFallbackUtils.buildErrorMessage("qbpmcockpit", status);

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
                    .processName(localizationService.getMessage(FEIGN_FALLBACK_EXTERNAL_SERVICE_ERROR, status, message))
                    .build();
            RestResponsePage<BpmInstanceDto> pageResponse = new RestResponsePage<>(List.of(errRes));
            pageResponse.setErrorStatus(status);
            pageResponse.setErrorMessage(message);
            return pageResponse;
        };
    }
}
