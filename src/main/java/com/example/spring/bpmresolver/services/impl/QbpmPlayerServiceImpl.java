package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.clients.QbpmPlayerClient;
import com.example.spring.bpmresolver.dto.BpmFinishedProcessResponseDto;
import com.example.spring.bpmresolver.services.QbpmPlayerService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QbpmPlayerServiceImpl implements QbpmPlayerService {

    private final QbpmPlayerClient qbpmPlayerClient;

    public QbpmPlayerServiceImpl(QbpmPlayerClient qbpmPlayerClient) {
        this.qbpmPlayerClient = qbpmPlayerClient;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_BPM')")
    public List<BpmFinishedProcessResponseDto> deleteInstances(List<String> ids) {
        return qbpmPlayerClient.deleteInstances(ids);
    }
}
