package com.example.spring.bpmresolver.repositories;

import com.example.spring.bpmresolver.entities.ResolverAccessUserSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResolverAccessUserSettingRepository extends JpaRepository<ResolverAccessUserSetting, Long> {

    Optional<ResolverAccessUserSetting> findByUsername(String username);

    void deleteByUsername(String username);
}
