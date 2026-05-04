package com.example.spring.bpmresolver.repositories;

import com.example.spring.bpmresolver.entities.BpmFinishedProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BpmFinishedProcessRepository extends JpaRepository<BpmFinishedProcess, Long> {

    Page<BpmFinishedProcess> findAllByFinishedByOrderByFinishedAtDesc(String finishedBy, Pageable pageable);

    Optional<BpmFinishedProcess> findByIdAndFinishedBy(Long id, String finishedBy);

    Optional<BpmFinishedProcess> findFirstByProcessInstanceIdAndFinishedByOrderByFinishedAtDesc(String processInstanceId, String finishedBy);

    @Query("""
            select b
            from BpmFinishedProcess b
            where b.finishedBy = :finishedBy
              and (coalesce(:processInstanceId, '') = '' or b.processInstanceId like concat('%', :processInstanceId, '%'))
              and (coalesce(:status, '') = '' or b.status like concat('%', :status, '%'))
              and (coalesce(:message, '') = '' or b.message like concat('%', :message, '%'))
              and (b.finishedAt >= coalesce(:fromFinishedAt, b.finishedAt))
              and (b.finishedAt <= coalesce(:toFinishedAt, b.finishedAt))
            order by b.finishedAt desc
            """)
    Page<BpmFinishedProcess> search(
            @Param("finishedBy") String finishedBy,
            @Param("processInstanceId") String processInstanceId,
            @Param("status") String status,
            @Param("message") String message,
            @Param("fromFinishedAt") LocalDateTime fromFinishedAt,
            @Param("toFinishedAt") LocalDateTime toFinishedAt,
            Pageable pageable
    );

    @Modifying
    @Query("delete from BpmFinishedProcess b where b.id in :ids and b.finishedBy = :finishedBy")
    int deleteAllByIdInAndFinishedBy(@Param("ids") List<Long> ids, @Param("finishedBy") String finishedBy);
}
