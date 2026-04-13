package com.example.spring.bpmresolver.actuators;

import com.example.spring.bpmresolver.entities.UserAccount;
import com.example.spring.bpmresolver.repositories.UserAccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class UsersHealthIndicator implements HealthIndicator {

    private final UserAccountRepository repository;

    @Override
    public Health health() {
        Optional<UserAccount> user = repository.findByUsername("admin");
        if (user.isEmpty()) {
            return Health.down()
                    .status(Status.DOWN)
                    .withDetail("message", "Отсутствует технический пользователь администратора! Сервис не сможет нормально работать без администратора!")
                    .build();
        } else {
            return Health.up().withDetail("message", "Администратор на месте!").build();
        }
    }
}
