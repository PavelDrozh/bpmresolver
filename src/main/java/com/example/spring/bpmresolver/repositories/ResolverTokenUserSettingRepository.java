package com.example.spring.bpmresolver.repositories;

import com.example.spring.bpmresolver.entities.ResolverTokenUserSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResolverTokenUserSettingRepository extends JpaRepository<ResolverTokenUserSetting, Long> {

    Optional<ResolverTokenUserSetting> findByUsername(String username);

    void deleteByUsername(String username);
}
