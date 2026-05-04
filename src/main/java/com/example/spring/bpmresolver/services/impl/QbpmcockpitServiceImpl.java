package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.clients.QbpmcockpitFeignClient;
import com.example.spring.bpmresolver.dto.BpmFinishedProcessResponseDto;
import com.example.spring.bpmresolver.dto.BpmInstanceDto;
import com.example.spring.bpmresolver.dto.QbpmcockpitInstancesRequest;
import com.example.spring.bpmresolver.dto.RestResponsePage;
import com.example.spring.bpmresolver.services.BpmFinishedProcessService;
import com.example.spring.bpmresolver.services.QbpmcockpitService;
import com.example.spring.bpmresolver.services.QbpmPlayerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QbpmcockpitServiceImpl implements QbpmcockpitService {

    private final QbpmcockpitFeignClient qbpmcockpitFeignClient;
    private final QbpmPlayerService qbpmPlayerService;
    private final BpmFinishedProcessService bpmFinishedProcessService;

    public QbpmcockpitServiceImpl(QbpmcockpitFeignClient qbpmcockpitFeignClient,
                                  QbpmPlayerService qbpmPlayerService,
                                  BpmFinishedProcessService bpmFinishedProcessService) {
        this.qbpmcockpitFeignClient = qbpmcockpitFeignClient;
        this.qbpmPlayerService = qbpmPlayerService;
        this.bpmFinishedProcessService = bpmFinishedProcessService;
    }

    @Override
    public RestResponsePage<BpmInstanceDto> getInstances(QbpmcockpitInstancesRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        int size = request.getSize();
        RestResponsePage<BpmInstanceDto> result = qbpmcockpitFeignClient.getInstances(
                request.getProcessName(),
                request.getState(),
                request.getServiceName(),
                request.getUserLogin(),
                request.getLastStartDate(),
                request.getLastEndDate(),
                request.getBusinessKey(),
                request.getIsRoot(),
                request.getWithOpenIncidents(),
                request.getTenantId(),
                request.getSort(),
                request.getPage(),
                size
        );

        //тут урезаем ответ от сервиса ввиду его некорректной работы
        // (на 0 странице size = 20, на 1 - 40, 2 - 60 и тд)
        List<BpmInstanceDto> content = result.getContent().stream().limit(size).toList();
        return new RestResponsePage<>(content,result.getPageable(),result.getTotalElements());
    }

    @Override
    public List<BpmFinishedProcessResponseDto> finishInstances(List<String> ids, String finishedBy) {
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        List<BpmFinishedProcessResponseDto> response = qbpmPlayerService.deleteInstances(ids);
        bpmFinishedProcessService.saveFinishResults(response, finishedBy);
        return response;
    }
}
