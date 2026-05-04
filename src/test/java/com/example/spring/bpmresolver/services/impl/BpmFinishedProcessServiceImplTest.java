package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.dto.BpmFinishedProcessResponseDto;
import com.example.spring.bpmresolver.entities.BpmFinishedProcess;
import com.example.spring.bpmresolver.repositories.BpmFinishedProcessRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BpmFinishedProcessServiceImplTest {

    @Mock
    private BpmFinishedProcessRepository repository;

    @InjectMocks
    private BpmFinishedProcessServiceImpl service;

    @Captor
    private ArgumentCaptor<List<BpmFinishedProcess>> entitiesCaptor;

    @Test
    void saveFinishResults_whenNull_returnsEmptyAndDoesNotCallRepository() {
        List<BpmFinishedProcess> result = service.saveFinishResults(null, "me");
        assertThat(result).isEmpty();
        verifyNoInteractions(repository);
    }

    @Test
    void saveFinishResults_whenEmpty_returnsEmptyAndDoesNotCallRepository() {
        List<BpmFinishedProcess> result = service.saveFinishResults(List.of(), "me");
        assertThat(result).isEmpty();
        verifyNoInteractions(repository);
    }

    @Test
    void saveFinishResults_mapsDtosAndSaves() {
        List<BpmFinishedProcessResponseDto> dtos = List.of(
                BpmFinishedProcessResponseDto.builder().id("p1").status("OK").message("m1").build(),
                BpmFinishedProcessResponseDto.builder().id("p2").status("ERR").message("m2").build()
        );

        when(repository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        List<BpmFinishedProcess> saved = service.saveFinishResults(dtos, "me");

        assertThat(saved).hasSize(2);
        verify(repository).saveAll(entitiesCaptor.capture());

        List<BpmFinishedProcess> entities = entitiesCaptor.getValue();
        assertThat(entities).hasSize(2);
        assertThat(entities.get(0).getProcessInstanceId()).isEqualTo("p1");
        assertThat(entities.get(0).getFinishedBy()).isEqualTo("me");
        assertThat(entities.get(0).getStatus()).isEqualTo("OK");
        assertThat(entities.get(0).getMessage()).isEqualTo("m1");
        assertThat(entities.get(0).getFinishedAt()).isNotNull();
    }

    @Test
    void findAll_delegatesToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BpmFinishedProcess> expected = new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAllByFinishedByOrderByFinishedAtDesc("me", pageable)).thenReturn(expected);

        Page<BpmFinishedProcess> actual = service.findAll("me", pageable);

        assertThat(actual).isSameAs(expected);
    }

    @Test
    void search_delegatesToRepository() {
        Pageable pageable = PageRequest.of(1, 5);
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now();
        Page<BpmFinishedProcess> expected = new PageImpl<>(List.of(), pageable, 0);

        when(repository.search("me", "p", "s", "m", from, to, pageable)).thenReturn(expected);

        Page<BpmFinishedProcess> actual = service.search("me", "p", "s", "m", from, to, pageable);

        assertThat(actual).isSameAs(expected);
    }

    @Test
    void findById_delegatesToRepository() {
        Optional<BpmFinishedProcess> expected = Optional.of(BpmFinishedProcess.builder().id(1L).build());
        when(repository.findByIdAndFinishedBy(1L, "me")).thenReturn(expected);

        assertThat(service.findById(1L, "me")).isSameAs(expected);
    }

    @Test
    void findLatestByProcessInstanceId_delegatesToRepository() {
        Optional<BpmFinishedProcess> expected = Optional.of(BpmFinishedProcess.builder().id(2L).build());
        when(repository.findFirstByProcessInstanceIdAndFinishedByOrderByFinishedAtDesc("p1", "me")).thenReturn(expected);

        assertThat(service.findLatestByProcessInstanceId("p1", "me")).isSameAs(expected);
    }

    @Test
    void deleteBatch_whenNullOrEmpty_returns0AndDoesNotCallRepository() {
        assertThat(service.deleteBatch(null, "me")).isZero();
        assertThat(service.deleteBatch(List.of(), "me")).isZero();
        verifyNoInteractions(repository);
    }

    @Test
    void deleteBatch_delegatesToRepository() {
        when(repository.deleteAllByIdInAndFinishedBy(List.of(1L, 2L), "me")).thenReturn(2);

        int deleted = service.deleteBatch(List.of(1L, 2L), "me");

        assertThat(deleted).isEqualTo(2);
    }
}
