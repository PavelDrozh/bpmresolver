package com.example.spring.bpmresolver.clients;

import com.example.spring.bpmresolver.dto.BpmFinishedProcessResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "qbpmPlayerClient",
        url = "${app.qbpmplayer.base-url:qesevaluationsbpm}",
        path = "/${app.qbpmplayer.context:qesevaluationsbpm}",
        configuration = QbpmPlayerConfig.class,
        fallbackFactory = QbpmPlayerClientFallbackFactory.class
)
public interface QbpmPlayerClient {

    @DeleteMapping("/v1/process-instances")
    List<BpmFinishedProcessResponseDto> deleteInstances(
            @RequestParam(value = "id", required = false) List<String> ids
    );
}

