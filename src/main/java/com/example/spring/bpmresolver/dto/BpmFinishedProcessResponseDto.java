package com.example.spring.bpmresolver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BpmFinishedProcessResponseDto {

    private String id;
    private String status;
    private String message;
}
