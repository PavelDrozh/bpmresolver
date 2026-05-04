package com.example.spring.bpmresolver.repositories;

import com.example.spring.bpmresolver.entities.QbpmplayerUserSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QbpmplayerUserSettingRepository extends JpaRepository<QbpmplayerUserSetting, Long> {
    Optional<QbpmplayerUserSetting> findByUsername(String username);

    void deleteByUsername(String username);
}
