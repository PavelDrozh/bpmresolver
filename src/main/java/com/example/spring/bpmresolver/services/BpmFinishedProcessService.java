package com.example.spring.bpmresolver.services;

import com.example.spring.bpmresolver.dto.BpmFinishedProcessResponseDto;
import com.example.spring.bpmresolver.entities.BpmFinishedProcess;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BpmFinishedProcessService {
    List<BpmFinishedProcess> saveFinishResults(List<BpmFinishedProcessResponseDto> results, String finishedBy);

    Page<BpmFinishedProcess> findAll(String finishedBy, Pageable pageable);

    Page<BpmFinishedProcess> search(
            String finishedBy,
            String processInstanceId,
            String status,
            String message,
            LocalDateTime fromFinishedAt,
            LocalDateTime toFinishedAt,
            Pageable pageable
    );

    Optional<BpmFinishedProcess> findById(Long id, String finishedBy);

    Optional<BpmFinishedProcess> findLatestByProcessInstanceId(String processInstanceId, String finishedBy);

    int deleteBatch(List<Long> ids, String finishedBy);
}
