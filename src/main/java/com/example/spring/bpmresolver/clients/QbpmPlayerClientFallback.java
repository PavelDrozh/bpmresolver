package com.example.spring.bpmresolver.clients;

import com.example.spring.bpmresolver.dto.BpmFinishedProcessResponseDto;
import com.example.spring.bpmresolver.localization.LocalizationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static com.example.spring.bpmresolver.localization.LocalizationServiceImpl.QBPM_PLAYER_FALLBACK_GENERIC_ERROR;

@Component
@AllArgsConstructor
public class QbpmPlayerClientFallback implements QbpmPlayerClient {

    private final LocalizationService localizationService;

    @Override
    public List<BpmFinishedProcessResponseDto> deleteInstances(String context, List<String> ids) {
        return List.of(BpmFinishedProcessResponseDto.builder()
                .id(UUID.randomUUID().toString())
                .status("ERROR")
                .message(localizationService.getMessage(QBPM_PLAYER_FALLBACK_GENERIC_ERROR))
                .build());
    }
}
