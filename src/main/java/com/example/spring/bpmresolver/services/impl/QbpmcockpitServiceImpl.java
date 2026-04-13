package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.clients.QbpmcockpitFeignClient;
import com.example.spring.bpmresolver.dto.BpmInstanceDto;
import com.example.spring.bpmresolver.dto.RestResponsePage;
import com.example.spring.bpmresolver.services.QbpmcockpitService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QbpmcockpitServiceImpl implements QbpmcockpitService {

    private final QbpmcockpitFeignClient qbpmcockpitClient;

    public QbpmcockpitServiceImpl(QbpmcockpitFeignClient qbpmcockpitClient) {
        this.qbpmcockpitClient = qbpmcockpitClient;
    }

    @Override
    public RestResponsePage<BpmInstanceDto> getInstances(
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
    ) {
        RestResponsePage<BpmInstanceDto> result = qbpmcockpitClient.getInstances(
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
        );

        //тут урезаем ответ от сервиса ввиду его некорректной работы
        // (на 0 странице size = 20, на 1 - 40, 2 - 60 и тд)
        List<BpmInstanceDto> content = result.getContent().stream().limit(size).toList();
        return new RestResponsePage<>(content,result.getPageable(),result.getTotalElements());
    }
}
