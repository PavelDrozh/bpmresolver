package com.example.spring.bpmresolver.services;

import com.example.spring.bpmresolver.dto.BpmFinishedProcessResponseDto;

import java.util.List;

public interface QbpmPlayerService {

    List<BpmFinishedProcessResponseDto> deleteInstances(List<String> ids);
}
