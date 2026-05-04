package com.example.spring.bpmresolver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class QbpmcockpitInstancesRequest {

    private String processName;
    private String state;
    private String serviceName;
    private String userLogin;
    private String lastStartDate;
    private String lastEndDate;
    private String businessKey;
    private Boolean isRoot;
    private Boolean withOpenIncidents;
    private String tenantId;
    private String sort;
    private int page;
    private int size;
}
