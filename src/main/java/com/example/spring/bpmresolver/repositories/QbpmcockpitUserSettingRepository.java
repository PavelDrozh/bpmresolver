package com.example.spring.bpmresolver.repositories;

import com.example.spring.bpmresolver.entities.QbpmcockpitUserSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QbpmcockpitUserSettingRepository extends JpaRepository<QbpmcockpitUserSetting, Long> {
    Optional<QbpmcockpitUserSetting> findByUsername(String username);

    void deleteByUsername(String username);
}
