package com.example.spring.bpmresolver.services;

import com.example.spring.bpmresolver.dto.BpmFinishedProcessResponseDto;
import com.example.spring.bpmresolver.dto.BpmInstanceDto;
import com.example.spring.bpmresolver.dto.QbpmcockpitInstancesRequest;
import com.example.spring.bpmresolver.dto.RestResponsePage;

import java.util.List;

public interface QbpmcockpitService {

    RestResponsePage<BpmInstanceDto> getInstances(QbpmcockpitInstancesRequest request);

    List<BpmFinishedProcessResponseDto> finishInstances(List<String> ids, String finishedBy);
}
