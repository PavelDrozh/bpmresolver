package com.example.spring.bpmresolver.services;

import com.example.spring.bpmresolver.dto.BpmInstanceDto;
import com.example.spring.bpmresolver.dto.RestResponsePage;

public interface QbpmcockpitService {

    RestResponsePage<BpmInstanceDto> getInstances(
            String processName,
            String state,
            String serviceName,
            String userLogin,
            String lastStartDate,
            String lastEndDate,
            String businessKey,
            Boolean isRoot,
            Boolean withOpenIncidents,
            String tenantId,
            String sort,
            int page,
            int size
    );
}
