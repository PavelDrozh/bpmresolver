package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.dto.BpmFinishedProcessResponseDto;
import com.example.spring.bpmresolver.entities.BpmFinishedProcess;
import com.example.spring.bpmresolver.repositories.BpmFinishedProcessRepository;
import com.example.spring.bpmresolver.services.BpmFinishedProcessService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BpmFinishedProcessServiceImpl implements BpmFinishedProcessService {

    private final BpmFinishedProcessRepository repository;

    @Override
    public List<BpmFinishedProcess> saveFinishResults(List<BpmFinishedProcessResponseDto> results, String finishedBy) {
        if (results == null || results.isEmpty()) {
            return List.of();
        }

        LocalDateTime now = LocalDateTime.now();
        List<BpmFinishedProcess> entities = results.stream()
                .map(r -> BpmFinishedProcess.builder()
                        .processInstanceId(r.getId())
                        .finishedBy(finishedBy)
                        .status(r.getStatus())
                        .message(r.getMessage())
                        .finishedAt(now)
                        .build())
                .toList();

        return repository.saveAll(entities);
    }

    @Override
    public Page<BpmFinishedProcess> findAll(String finishedBy, Pageable pageable) {
        return repository.findAllByFinishedByOrderByFinishedAtDesc(finishedBy, pageable);
    }

    @Override
    public Page<BpmFinishedProcess> search(
            String finishedBy,
            String processInstanceId,
            String status,
            String message,
            LocalDateTime fromFinishedAt,
            LocalDateTime toFinishedAt,
            Pageable pageable
    ) {
        return repository.search(
                finishedBy,
                processInstanceId,
                status,
                message,
                fromFinishedAt,
                toFinishedAt,
                pageable
        );
    }

    @Override
    public Optional<BpmFinishedProcess> findById(Long id, String finishedBy) {
        return repository.findByIdAndFinishedBy(id, finishedBy);
    }

    @Override
    public Optional<BpmFinishedProcess> findLatestByProcessInstanceId(String processInstanceId, String finishedBy) {
        return repository.findFirstByProcessInstanceIdAndFinishedByOrderByFinishedAtDesc(processInstanceId, finishedBy);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_BPM')")
    public int deleteBatch(List<Long> ids, String finishedBy) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return repository.deleteAllByIdInAndFinishedBy(ids, finishedBy);
    }
}
