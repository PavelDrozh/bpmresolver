package com.example.spring.bpmresolver.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;

@Getter
@ConfigurationProperties(prefix = "app.qbpmplayer")
@AllArgsConstructor
public final class QbpmPlayerProperties {
    private final String baseUrl;
    private final String context;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (QbpmPlayerProperties) obj;
        return Objects.equals(this.baseUrl, that.baseUrl) &&
                Objects.equals(this.context, that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseUrl, context);
    }

    @Override
    public String toString() {
        return "QbpmPlayerProperties[" +
                "baseUrl=" + baseUrl + ", " +
                "context=" + context + ']';
    }
}
