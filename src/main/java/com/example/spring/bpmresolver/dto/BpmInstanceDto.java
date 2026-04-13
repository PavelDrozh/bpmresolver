package com.example.spring.bpmresolver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BpmInstanceDto {

    private Long duration;
    private String id;
    private String processId;
    private Long processVersion;
    private String endTime;
    private String startTime;
    private String state;
    private String stateName;
    private String processName;
    private String processKey;
    private String serviceName;
    private String rootProcessInstanceId;
    private String superProcessInstanceId;
    private String superCaseInstanceId;
    private String caseInstanceId;
    private String key;
    private String removalTime;
    private String startUserId;
    private String startActivityId;
    private String endActivityId;
    private String deleteReason;
    private String tenantId;
    private String correlationId;
    private String userLogin;
    private String businessKey;
    private Long variablesSize;
    private String deploymentVersion;
    private Boolean withOpenIncidents;
    private String traceId;
    private String deploymentTime;
}
