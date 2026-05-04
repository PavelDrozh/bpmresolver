package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.clients.QbpmPlayerClient;
import com.example.spring.bpmresolver.clients.QbpmPlayerClientRouter;
import com.example.spring.bpmresolver.dto.BpmFinishedProcessResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QbpmPlayerServiceImplTest {

    @Mock
    private QbpmPlayerClientRouter qbpmPlayerClientRouter;

    @Mock
    private QbpmPlayerClient qbpmPlayerClient;

    @InjectMocks
    private QbpmPlayerServiceImpl service;

    @Test
    void deleteInstances_delegatesToClient() {
        List<BpmFinishedProcessResponseDto> expected = List.of(BpmFinishedProcessResponseDto.builder().id("1").build());
        when(qbpmPlayerClientRouter.client()).thenReturn(qbpmPlayerClient);
        when(qbpmPlayerClientRouter.context()).thenReturn("qwe");
        when(qbpmPlayerClient.deleteInstances("qwe", List.of("1"))).thenReturn(expected);

        List<BpmFinishedProcessResponseDto> actual = service.deleteInstances(List.of("1"));

        assertThat(actual).isSameAs(expected);
    }
}
