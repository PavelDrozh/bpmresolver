package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.clients.QbpmPlayerClientRouter;
import com.example.spring.bpmresolver.dto.BpmFinishedProcessResponseDto;
import com.example.spring.bpmresolver.services.QbpmPlayerService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QbpmPlayerServiceImpl implements QbpmPlayerService {

    private final QbpmPlayerClientRouter qbpmPlayerClientRouter;

    public QbpmPlayerServiceImpl(QbpmPlayerClientRouter qbpmPlayerClientRouter) {
        this.qbpmPlayerClientRouter = qbpmPlayerClientRouter;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_BPM')")
    public List<BpmFinishedProcessResponseDto> deleteInstances(List<String> ids) {
        return qbpmPlayerClientRouter.client().deleteInstances(qbpmPlayerClientRouter.context(), ids);
    }
}
