package com.example.spring.bpmresolver.controllers.rest;

import com.example.spring.bpmresolver.dto.BpmInstanceDto;
import com.example.spring.bpmresolver.dto.QbpmcockpitInstancesRequest;
import com.example.spring.bpmresolver.dto.RestResponsePage;
import com.example.spring.bpmresolver.localization.LocalizationService;
import com.example.spring.bpmresolver.services.QbpmcockpitService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static com.example.spring.bpmresolver.localization.LocalizationServiceImpl.QBPMCOCKPIT_INSTANCES_REQUEST_LOG;

@RestController
@RequestMapping("/api/qbpmcockpit")
@AllArgsConstructor
@Slf4j
public class QbpmcockpitController {

    private final QbpmcockpitService qbpmcockpitService;
    private final LocalizationService localizationService;

    @GetMapping(value = "/instances", produces = MediaType.APPLICATION_JSON_VALUE)
    public RestResponsePage<BpmInstanceDto> instances(
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
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size
    ) {
        log.info(localizationService.getMessage(
                QBPMCOCKPIT_INSTANCES_REQUEST_LOG,
                processName,
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
                size
        ));
        return qbpmcockpitService.getInstances(QbpmcockpitInstancesRequest.builder()
                .processName(processName)
                .state(state)
                .serviceName(serviceName)
                .userLogin(userLogin)
                .lastStartDate(lastStartDate)
                .lastEndDate(lastEndDate)
                .businessKey(businessKey)
                .isRoot(isRoot)
                .withOpenIncidents(withOpenIncidents)
                .tenantId(tenantId)
                .sort(sort)
                .page(page)
                .size(size)
                .build());
    }
}
