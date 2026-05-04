package com.example.spring.bpmresolver.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Objects;

@Getter
@ConfigurationProperties(prefix = "app.routing")
@AllArgsConstructor
public final class RoutingProperties {

    private final List<String> qbpmplayerAllowedServiceIds;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (RoutingProperties) obj;
        return Objects.equals(this.qbpmplayerAllowedServiceIds, that.qbpmplayerAllowedServiceIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qbpmplayerAllowedServiceIds);
    }

    @Override
    public String toString() {
        return "RoutingProperties[" +
                "qbpmplayerAllowedServiceIds=" + qbpmplayerAllowedServiceIds + ']';
    }
}
