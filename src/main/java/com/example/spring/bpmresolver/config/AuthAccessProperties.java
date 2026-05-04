package com.example.spring.bpmresolver.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;

@Getter
@ConfigurationProperties(prefix = "app.auth.access")
@AllArgsConstructor
public final class AuthAccessProperties {
    private final String realm;
    private final String clientId;
    private final String userName;
    private final String url;


    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AuthAccessProperties) obj;
        return Objects.equals(this.realm, that.realm) &&
                Objects.equals(this.clientId, that.clientId) &&
                Objects.equals(this.userName, that.userName) &&
                Objects.equals(this.url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(realm, clientId, userName, url);
    }
}
