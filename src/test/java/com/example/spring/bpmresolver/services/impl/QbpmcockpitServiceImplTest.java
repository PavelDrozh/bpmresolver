package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.clients.QbpmcockpitFeignClient;
import com.example.spring.bpmresolver.dto.BpmInstanceDto;
import com.example.spring.bpmresolver.dto.RestResponsePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QbpmcockpitServiceImplTest {

    @Mock
    private QbpmcockpitFeignClient qbpmcockpitClient;

    @InjectMocks
    private QbpmcockpitServiceImpl service;

    @Test
    void getInstances_trimsContentToSize() {
        List<BpmInstanceDto> content = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            content.add(BpmInstanceDto.builder().id("id-" + i).build());
        }
        RestResponsePage<BpmInstanceDto> remote = new RestResponsePage<>(content, PageRequest.of(0, 50), 50);

        when(qbpmcockpitClient.getInstances(
                null, null, null, null, null, null, null,
                null, null, null, null, 0, 20
        )).thenReturn(remote);

        RestResponsePage<BpmInstanceDto> result = service.getInstances(
                null, null, null, null, null, null, null,
                null, null, null, null, 0, 20
        );

        assertThat(result.getContent()).hasSize(20);
        assertThat(result.getTotalElements()).isEqualTo(50);
    }
}
