package com.example.spring.bpmresolver.clients;

import com.example.spring.bpmresolver.dto.BpmInstanceDto;
import com.example.spring.bpmresolver.dto.RestResponsePage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(
        name = "${app.qbpmcockpit.service:qbpmcockpit}",
        path = "/${app.qbpmcockpit.context:qbpmcockpit}",
        configuration = QbpmcockpitFeignConfig.class,
        fallbackFactory = QbpmcockpitFeignClientFallbackFactory.class
)
public interface QbpmcockpitFeignClient {

    @GetMapping("/v1/default/instances")
    RestResponsePage<BpmInstanceDto> getInstances(
            @RequestParam(value = "processName", required = false) String processName,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "serviceName", required = false) String serviceName,
            @RequestParam(value = "userLogin", required = false) String userLogin,
            @RequestParam(value = "lastStartDate", required = false) String lastStartDate,
            @RequestParam(value = "lastEndDate", required = false) String lastEndDate,
            @RequestParam(value = "businessKey", required = false) String businessKey,
            @RequestParam(value = "isRoot", required = false) Boolean isRoot,
            @RequestParam(value = "withOpenIncidents", required = false) Boolean withOpenIncidents,
            @RequestParam(value = "tenantId", required = false) String tenantId,
            @RequestParam(value = "sort", required = false, defaultValue = "startTime,desc") String sort,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size
    );
}
