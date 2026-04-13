package com.example.spring.bpmresolver.actuators;

import com.example.spring.bpmresolver.entities.UserAccount;
import com.example.spring.bpmresolver.entities.UserRole;
import com.example.spring.bpmresolver.repositories.UserRoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class RolesHealthIndicator implements HealthIndicator {

    private final UserRoleRepository repository;

    @Override
    public Health health() {
        Optional<UserRole> roleBpm = repository.findByRole("ROLE_BPM");
        Optional<UserRole> roleAdmin = repository.findByRole("ROLE_ADMIN");
        if (roleBpm.isEmpty() || roleAdmin.isEmpty()) {
            return Health.down()
                    .status(Status.DOWN)
                    .withDetail("message", "Отсутствует роли необходимые для корректной работы сервиса!")
                    .build();
        } else {
            return Health.up().withDetail("message", "Роли на месте!").build();
        }
    }
}
