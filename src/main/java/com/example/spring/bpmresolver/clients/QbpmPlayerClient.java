package com.example.spring.bpmresolver.clients;

import com.example.spring.bpmresolver.dto.BpmFinishedProcessResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "${app.qbpmplayer.service:qesevaluationsbpm}",
        configuration = QbpmPlayerConfig.class,
        fallback = QbpmPlayerClientFallback.class
)
public interface QbpmPlayerClient {

    @DeleteMapping("/{context}/v1/process-instances")
    List<BpmFinishedProcessResponseDto> deleteInstances(
            @PathVariable("context") String context,
            @RequestParam(value = "id", required = false) List<String> ids
    );
}

