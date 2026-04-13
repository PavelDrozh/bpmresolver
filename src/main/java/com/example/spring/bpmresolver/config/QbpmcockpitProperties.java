package com.example.spring.bpmresolver.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;

@Getter
@ConfigurationProperties(prefix = "app.qbpmcockpit")
@AllArgsConstructor
public final class QbpmcockpitProperties {
    private final String baseUrl;
    private final String token;


    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (QbpmcockpitProperties) obj;
        return Objects.equals(this.baseUrl, that.baseUrl) &&
                Objects.equals(this.token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseUrl, token);
    }

    @Override
    public String toString() {
        return "QbpmcockpitProperties[" +
                "baseUrl=" + baseUrl + ", " +
                "token=" + token + ']';
    }
}
