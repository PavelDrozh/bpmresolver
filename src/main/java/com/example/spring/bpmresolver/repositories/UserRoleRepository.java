package com.example.spring.bpmresolver.repositories;

import com.example.spring.bpmresolver.entities.UserRole;
import com.example.spring.bpmresolver.entities.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    List<UserRole> findAllByUserId(Long userId);

    Optional<UserRole> findByRole(String role);
}
