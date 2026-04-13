package com.example.spring.bpmresolver.controllers.rest;

import com.example.spring.bpmresolver.entities.BpmFinishedProcess;
import com.example.spring.bpmresolver.services.BpmFinishedProcessService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/finished-processes")
@AllArgsConstructor
public class BpmFinishedProcessController {

    private final BpmFinishedProcessService service;

    private static String requireUser(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null || jwt.getSubject().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return jwt.getSubject();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<BpmFinishedProcess> list(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String finishedBy = requireUser(jwt);
        Pageable pageable = PageRequest.of(page, size);
        return service.findAll(finishedBy, pageable);
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<BpmFinishedProcess> search(
            @RequestParam(value = "processInstanceId", required = false) String processInstanceId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "message", required = false) String message,
            @RequestParam(value = "fromFinishedAt", required = false) LocalDateTime fromFinishedAt,
            @RequestParam(value = "toFinishedAt", required = false) LocalDateTime toFinishedAt,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String finishedBy = requireUser(jwt);
        Pageable pageable = PageRequest.of(page, size);
        return service.search(
                finishedBy,
                processInstanceId,
                status,
                message,
                fromFinishedAt,
                toFinishedAt,
                pageable
        );
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public BpmFinishedProcess getById(@PathVariable("id") Long id, @AuthenticationPrincipal Jwt jwt) {
        String finishedBy = requireUser(jwt);
        return service.findById(id, finishedBy)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BpmFinishedProcess not found: " + id));
    }

    @GetMapping(value = "/by-process-instance/{processInstanceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public BpmFinishedProcess getLatestByProcessInstanceId(
            @PathVariable("processInstanceId") String processInstanceId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String finishedBy = requireUser(jwt);
        return service.findLatestByProcessInstanceId(processInstanceId, finishedBy)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BpmFinishedProcess not found for processInstanceId: " + processInstanceId));
    }

    public record DeleteBatchRequest(List<Long> ids) {}

    public record DeleteBatchResponse(int deletedCount) {}

    @PostMapping(value = "/delete-batch", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DeleteBatchResponse deleteBatch(@RequestBody DeleteBatchRequest request, @AuthenticationPrincipal Jwt jwt) {
        if (request == null || request.ids() == null || request.ids().isEmpty()) {
            return new DeleteBatchResponse(0);
        }
        String finishedBy = requireUser(jwt);
        int deleted = service.deleteBatch(request.ids(), finishedBy);
        return new DeleteBatchResponse(deleted);
    }
}
