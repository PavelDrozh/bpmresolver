package com.example.spring.bpmresolver.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "bpm_finished_process")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BpmFinishedProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "process_instance_id", nullable = false)
    private String processInstanceId;

    @Column(name = "finished_by")
    private String finishedBy;

    @Column(name = "status")
    private String status;

    @Column(name = "message", length = 4000)
    private String message;

    @Column(name = "finished_at", nullable = false)
    private LocalDateTime finishedAt;
}
